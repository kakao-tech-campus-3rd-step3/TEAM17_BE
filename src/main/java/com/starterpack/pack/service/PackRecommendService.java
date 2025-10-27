package com.starterpack.pack.service;

import com.starterpack.pack.dto.PackRecommendDto;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.repository.PackRepository;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PackRecommendService {

    private final PackRepository packRepository;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final int    N_LIKE_TIMES = 200;      // decayAverage용 최근 N개
    private static final double HALF_LIFE_HOURS = 24.0;  // 감쇠 반감기(시간)

    public List<PackRecommendDto> getTodayTop3() {
        List<Pack> packs = packRepository.findAll();
        if (packs.isEmpty()) return List.of();

        LocalDateTime nowLdt = LocalDateTime.now(ZONE);
        Instant now = nowLdt.atZone(ZONE).toInstant();
        LocalDateTime from24h = nowLdt.minusHours(24);
        LocalDateTime from48h = nowLdt.minusHours(48);
        LocalDateTime from7d  = nowLdt.minusDays(7);
        LocalDate today = nowLdt.toLocalDate();
        LocalDateTime from60d = nowLdt.minusDays(60);

        record F(Pack p, long Ltotal, long L24h, long L7d, long Lprev24h,
                 long streakDays, long hoursSinceLastLike, List<Instant> likeTimes) {}

        List<F> feats = new ArrayList<>(packs.size());
        for (Pack p : packs) {
            Long pid = p.getId();

            long Ltotal   = Optional.ofNullable(p.getPackLikeCount()).orElse(0);
            long L24h     = packRepository.countLikesSince(pid, from24h);
            long L7d      = packRepository.countLikesSince(pid, from7d);
            long Lprev24h = packRepository.countLikesBetween(pid, from48h, from24h);

            long hoursSinceLastLike = packRepository.findLastLikeTime(pid)
                    .map(t -> Duration.between(t.atZone(ZONE).toInstant(), now).toHours())
                    .map(h -> Math.max(0, h))
                    .orElse(Long.MAX_VALUE);

            List<Instant> likeTimes = packRepository
                    .findRecentLikeTimes(pid, PageRequest.of(0, N_LIKE_TIMES))
                    .stream().map(t -> t.atZone(ZONE).toInstant()).toList();

            long streakDays = computeStreakDays(
                    packRepository.findLikeTimesSince(pid, from60d)
                            .stream().map(LocalDateTime::toLocalDate).toList(),
                    today
            );

            feats.add(new F(p, Ltotal, L24h, L7d, Lprev24h, streakDays, hoursSinceLastLike, likeTimes));
        }

        // 퍼센타일(5/95)
        List<Long> arrLtotal = feats.stream().map(f -> f.Ltotal).toList();
        List<Long> arrL24h   = feats.stream().map(f -> f.L24h).toList();
        List<Double>arrRate  = feats.stream().map(f -> f.L7d > 0 ? (double) f.L24h / (double) f.L7d : 0.0).toList();
        List<Long> arrAccel  = feats.stream().map(f -> Math.max(0, f.L24h - f.Lprev24h)).toList();
        List<Long> arrStreak = feats.stream().map(f -> f.streakDays).toList();

        double p5_Ltotal   = percentileLong(arrLtotal, 0.05);
        double p95_Ltotal  = percentileLong(arrLtotal, 0.95);
        double p5_L24h     = percentileLong(arrL24h,   0.05);
        double p95_L24h    = percentileLong(arrL24h,   0.95);
        double p5_rate     = percentileDouble(arrRate,  0.05);
        double p95_rate    = percentileDouble(arrRate,  0.95);
        double p5_accel    = percentileLong(arrAccel,   0.05);
        double p95_accel   = percentileLong(arrAccel,   0.95);
        double p5_streak   = percentileLong(arrStreak,  0.05);
        double p95_streak  = percentileLong(arrStreak,  0.95);

        // 점수 계산
        record S(Pack p, double score) {}
        List<S> scored = feats.stream().map(f -> {
            double score = computeScoreWithAvgDecay(
                    f.Ltotal, f.L24h, f.L7d, f.Lprev24h,
                    f.streakDays,
                    p5_Ltotal, p95_Ltotal,
                    p5_L24h,  p95_L24h,
                    p5_rate,  p95_rate,
                    p5_accel, p95_accel,
                    p5_streak,p95_streak,
                    HALF_LIFE_HOURS,
                    f.likeTimes, now
            );
            return new S(f.p, score);
        }).collect(Collectors.toList());

        // Top3
        return scored.stream()
                .sorted(Comparator.comparingDouble(S::score).reversed())
                .limit(3)
                .map(s -> PackRecommendDto.from(s.p, s.score))
                .toList();
    }

    // ───────────── LikeScore 로직 통합 ─────────────

    private static double computeScoreWithAvgDecay(
            long Ltotal, long L24h, long L7d, long Lprev24h,
            long streakDays,
            double p5_Ltotal, double p95_Ltotal,
            double p5_L24h,  double p95_L24h,
            double p5_rate,  double p95_rate,
            double p5_accel, double p95_accel,
            double p5_streak,double p95_streak,
            double halfLifeHours,
            List<Instant> likeTimes, Instant now
    ) {
        double rate  = (L7d > 0) ? ((double)L24h / (double)L7d) : 0.0;
        long accel   = Math.max(0, L24h - Lprev24h);

        double s_total  = lnorm(Ltotal, p5_Ltotal, p95_Ltotal);
        double s_24h    = lnorm(L24h,   p5_L24h,   p95_L24h);
        double s_rate   = rnorm(rate,   p5_rate,   p95_rate);
        double s_accel  = lnorm(accel,  p5_accel,  p95_accel);
        double s_streak = rnorm(streakDays, p5_streak, p95_streak);

        double raw = 0.35 * s_total
                + 0.35 * s_24h
                + 0.15 * s_rate
                + 0.10 * s_accel
                + 0.05 * s_streak;

        double Davg = decayAverage(likeTimes, now, halfLifeHours);
        return raw * Davg;
    }

    private static double rnorm(double x, double p5, double p95) {
        if (p95 - p5 < 1e-9) return 0.0;
        return Math.max(0, Math.min(1, (x - p5) / (p95 - p5)));
    }

    private static double lnorm(double x, double p5, double p95) {
        if (x <= p5) return 0.0;
        if (x >= p95) return 1.0;
        return (Math.log(x + 1) - Math.log(p5 + 1)) / (Math.log(p95 + 1) - Math.log(p5 + 1));
    }

    private static double decayAverage(List<Instant> likeTimes, Instant now, double halfLifeHours) {
        if (likeTimes == null || likeTimes.isEmpty()) return 0.0;
        double lambda = Math.log(2.0) / halfLifeHours;
        double sum = 0.0;
        for (Instant t : likeTimes) {
            long dt = Math.max(0, Duration.between(t, now).toHours());
            sum += Math.exp(-lambda * dt);
        }
        return sum / likeTimes.size();
    }


    private static long computeStreakDays(List<LocalDate> dates, LocalDate today) {
        if (dates.isEmpty()) return 0L;
        Set<LocalDate> set = new HashSet<>(dates);
        long streak = 0;
        LocalDate cur = today;
        while (set.contains(cur)) {
            streak++;
            cur = cur.minusDays(1);
        }
        return streak;
    }

    private static double percentileLong(List<Long> data, double q) {
        if (data.isEmpty()) return 0.0;
        List<Long> sorted = new ArrayList<>(data);
        sorted.sort(Long::compare);
        int idx = (int) Math.floor(q * (sorted.size() - 1));
        idx = Math.max(0, Math.min(idx, sorted.size() - 1));
        return sorted.get(idx);
    }

    private static double percentileDouble(List<Double> data, double q) {
        if (data.isEmpty()) return 0.0;
        List<Double> sorted = new ArrayList<>(data);
        sorted.sort(Double::compare);
        int idx = (int) Math.floor(q * (sorted.size() - 1));
        idx = Math.max(0, Math.min(idx, sorted.size() - 1));
        return sorted.get(idx);
    }
}
