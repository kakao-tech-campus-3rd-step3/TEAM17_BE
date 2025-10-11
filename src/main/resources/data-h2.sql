-- ------------------------------------------------------------
INSERT INTO member (email, password, name, nickname, is_active, role, provider, birth_date, gender, phone_number)
VALUES
('admin@test.com', '$2a$10$jznYgTod3Yf2cjYfwFgrRe7CGo7Ubx0hBwyYBi1046U.ZN.GTGm16', '관리자', '관리자1234567', TRUE, 'ADMIN', 'EMAIL', '2002-12-25', 'MALE', '010-1234-5678'),
('user@test.com',  '{noop}pass', '일반사용자', '일반사용자7654321', TRUE, 'USER', 'EMAIL', '2002-12-25', 'MALE', '010-1234-5678');

-- ------------------------------------------------------------
-- 1) 카테고리 (Category) Mock 데이터 (6개)
INSERT INTO category (name, src) VALUES
('베이킹', 'https://i.namu.wiki/i/8MZZehLGZ1TCO4G7sBivu6GwEpFxajfYyXJ-m-2SIdrIH-4_1amvSyW-6fWykumnu0koFi6LZGNMJLV1O9k7sg.webp'),
('캠핑', 'https://blog-static.kkday.com/ko/blog/wp-content/uploads/korea_camping_spot_5.jpg'),
('러닝', 'https://cdn-icons-png.flaticon.com/512/4112/4112938.png'),
('독서', 'https://img.khan.co.kr/lady/2020/04/20/l_2020042004000008300185672.jpg'),
('헬스', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0w-245t0fqP7arr89Qf-I6aEyO_6lBNZ_TA&s'),
('요리', 'https://media.istockphoto.com/id/1081422898/ko/%EC%82%AC%EC%A7%84/%ED%8C%AC-%ED%8A%80%EA%B9%80-%EC%98%A4%EB%A6%AC.jpg?s=612x612&w=0&k=20&c=OZBRZbLLnwfUO9NNjwzkK03C4iwtFv4kmey3pHhrJwQ=');

-- ------------------------------------------------------------
-- 2) 스타터팩 (Pack) Mock 데이터 (6개)
INSERT INTO pack (name, category_id, member_id, price, pack_like_count, pack_bookmark_count, pack_comment_count, main_image_url, description)
VALUES
    ('홈베이킹 입문 스타터팩', 1, 1, 100000, 80, 25, 5,
     'https://i.namu.wiki/i/8MZZehLGZ1TCO4G7sBivu6GwEpFxajfYyXJ-m-2SIdrIH-4_1amvSyW-6fWykumnu0koFi6LZGNMJLV1O9k7sg.webp',
     '베이킹을 처음 시작하는 분들을 위한 필수 도구 모음입니다. 쿠키, 케이크 등 기본적인 베이킹에 필요한 모든 것을 담았습니다.'),

    ('캠핑 초보 필수템', 2, 1, 200000, 150, 45, 12,
     'https://blog-static.kkday.com/ko/blog/wp-content/uploads/korea_camping_spot_5.jpg',
     '캠핑 처음 가시는 분들을 위한 기본 장비 세트입니다. 감성 캠핑을 위한 분위기 아이템과 실용적인 장비를 함께 구성했습니다.'),

    ('러닝 시작하기', 3, 1, 35000, 210, 80, 18,
     'https://cdn-icons-png.flaticon.com/512/4112/4112938.png',
     '건강한 러닝 생활을 위한 필수 용품 모음입니다. 편안한 러닝을 위한 액세서리들로 구성했습니다.'),

    ('독서 애호가 세트', 4, 1, 40000, 110, 30, 8,
     'https://img.khan.co.kr/lady/2020/04/20/l_2020042004000008300185672.jpg',
     '독서를 더욱 즐겁게 만들어줄 감성 소품들입니다. 책갈피부터 독서대까지 독서 시간을 풍요롭게!'),

    ('홈트레이닝 스타터', 5, 1, 120000, 180, 95, 22,
     'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0w-245t0fqP7arr89Qf-I6aEyO_6lBNZ_TA&s',
     '집에서 효과적으로 운동하기 위한 헬스 용품 세트입니다. 안전하고 효율적인 홈트를 위한 필수 아이템!'),

    ('집밥 요리 시작', 6, 1, 80000, 160, 70, 15,
     'https://media.istockphoto.com/id/1081422898/ko/%EC%82%AC%EC%A7%84/%ED%8C%AC-%ED%8A%80%EA%B9%80-%EC%98%A4%EB%A6%AC.jpg?s=612x612&w=0&k=20&c=OZBRZbLLnwfUO9NNjwzkK03C4iwtFv4kmey3pHhrJwQ=',
     '요리 초보자도 쉽게 시작할 수 있는 기본 조리 도구 세트입니다. 맛있는 집밥의 시작!');
-- ------------------------------------------------------------
-- 4) 팩 아이템 (PackItem) Mock 데이터
-- Pack 1: 홈베이킹 입문 스타터팩
INSERT INTO pack_item (pack_id, name, link_url, description, image_url)
VALUES
    (1, '스텐 원형 베이킹 채망',
     'https://www.coupang.com/vp/products/6164239115',
     '구운 빵이나 쿠키를 식히는 데 필수적인 채망입니다. 스테인리스 재질로 위생적이고 오래 사용할 수 있습니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/0b2d/93ee6787be53cb1d540f561a8243c7cf1b295e103c746fc3878300a8a93f.jpg'),

    (1, '컨벤셔널 오븐',
     'https://www.coupang.com/vp/products/6511908960',
     '가정용 베이킹에 최적화된 오븐입니다. 온도 조절이 쉽고 쿠키, 케이크, 빵 등 다양한 베이킹이 가능합니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/1025_amir_coupang_oct_80k/677a/13e2bb7a02934df34d38b2cfbda406c7f9befb761164be76e6ea3b5e2b2a.jpg'),

    (1, '실리콘 베이킹 매트',
     'https://www.coupang.com/vp/products/example1',
     '반죽이 눌러붙지 않아 편리하게 사용할 수 있는 실리콘 매트입니다. 쉬운 세척과 재사용이 가능합니다.',
     'https://via.placeholder.com/300x300?text=Baking+Mat');

-- Pack 2: 캠핑 초보 필수템
INSERT INTO pack_item (pack_id, name, link_url, description, image_url)
VALUES
    (2, 'LED 감성 캠핑 랜턴',
     'https://www.coupang.com/vp/products/8512052933',
     '밤 캠핑의 필수품! 은은한 LED 조명으로 감성적인 분위기를 연출할 수 있습니다. 배터리 사용으로 편리합니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/137506773661373-2113f02f-ea77-43c5-88e1-3d890fb5c4cb.jpg'),

    (2, '국산 참나무 장작',
     'https://www.coupang.com/vp/products/5381058739',
     '캠프파이어용 고품질 국산 참나무 장작입니다. 화력이 좋고 오래 타서 실용적입니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/1025_amir_coupang_oct_80k/bd16/8bc52d34430e208b53837946cd11eabdc1ce6bd6c9c69347a331142f820d.jpg'),

    (2, '접이식 캠핑 의자',
     'https://www.coupang.com/vp/products/example2',
     '가볍고 휴대가 간편한 접이식 의자입니다. 편안한 등받이로 장시간 앉아도 피로하지 않습니다.',
     'https://via.placeholder.com/300x300?text=Camping+Chair');

-- Pack 3: 러닝 시작하기
INSERT INTO pack_item (pack_id, name, link_url, description, image_url)
VALUES
    (3, '써머텍트 기능성 헤어밴드',
     'https://www.coupang.com/vp/products/5716566331',
     '땀 흡수가 뛰어나고 통풍이 잘 되는 기능성 헤어밴드입니다. 달릴 때 흘러내리는 머리카락을 고정해줍니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/248528520729619-fa9a4de4-adcc-4c27-a736-510cac393f0b.png'),

    (3, '노바핏 러닝 벨트 가방',
     'https://www.coupang.com/vp/products/8625423187',
     '핸드폰, 카드, 열쇠 등 소지품을 안전하게 보관할 수 있는 러닝 벨트입니다. 흔들림 없이 밀착됩니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/f91e/cbe73c27fc560bc593ac5221ff3a7be6cf84585fadbf76305632aaa8cf38.png');

-- Pack 4: 독서 애호가 세트
INSERT INTO pack_item (pack_id, name, link_url, description, image_url)
VALUES
    (4, '워너디스 반투명 책갈피',
     'https://www.coupang.com/vp/products/7163840572',
     '심플하고 세련된 디자인의 반투명 책갈피입니다. 여러 권의 책에 동시에 사용하기 좋은 세트 구성입니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/3294/284d2729b6d13f20807723e72a14ba08540c0b5a39f84889750fca2a4860.jpg'),

    (4, '코믈리 투명 독서대',
     'https://www.coupang.com/vp/products/7187172452',
     '목과 허리가 편안한 각도로 책을 읽을 수 있는 독서대입니다. 투명 아크릴 재질로 깔끔하고 견고합니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/36a4/73e4ff422545e2fbd0000cf829d102fbf81daf2d95709107972d0a8a5ae2.jpg'),

    (4, 'LED 북라이트',
     'https://www.coupang.com/vp/products/example4',
     '침대나 소파에서 독서할 때 유용한 클립형 북라이트입니다. 눈의 피로를 줄여주는 따뜻한 조명입니다.',
     'https://via.placeholder.com/300x300?text=Book+Light');

-- Pack 5: 홈트레이닝 스타터
INSERT INTO pack_item (pack_id, name, link_url, description, image_url)
VALUES
    (5, '제로투히어로 헬스 스트랩',
     'https://www.coupang.com/vp/products/2270488247',
     '풀업, 랫풀다운 등 당기기 운동에 필수적인 스트랩입니다. 손목 보호와 더 강한 그립력을 제공합니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/1637211059741349-6cecca3f-64a8-41d3-8f4e-c0a0989ab638.jpg'),

    (5, '제로투히어로 리프팅 벨트',
     'https://www.coupang.com/vp/products/5569057581',
     '스쿼트, 데드리프트 시 허리를 보호하는 리프팅 벨트입니다. 복압을 높여 안전하고 효과적인 운동이 가능합니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/1025_amir_coupang_oct_80k/9e6e/7297d5bec77ca6093e8ef4bdecd0f4ae8319f8b8c406bad2d593f866c5b8.jpg'),

    (5, '요가 매트',
     'https://www.coupang.com/vp/products/example5',
     '스트레칭과 홈트레이닝에 필수적인 요가 매트입니다. 쿠션감이 좋아 관절을 보호해줍니다.',
     'https://via.placeholder.com/300x300?text=Yoga+Mat');

-- Pack 6: 집밥 요리 시작
INSERT INTO pack_item (pack_id, name, link_url, description, image_url)
VALUES
    (6, '코멧 다이아몬드 코팅 프라이팬',
     'https://www.coupang.com/vp/products/1419414761',
     '음식이 눌러붙지 않는 다이아몬드 코팅 프라이팬입니다. 초보자도 쉽게 요리할 수 있습니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/517625530368447-19024890-d0c1-40b0-919f-8f257d50f130.jpg'),

    (6, '코멧 걸이형 양면 도마',
     'https://www.coupang.com/vp/products/7991722426',
     '위생적인 양면 도마로 육류와 채소를 구분해서 사용할 수 있습니다. 걸이형으로 보관이 편리합니다.',
     'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/2663855586682-dade83ce-1801-47df-8c99-bce4cca91a90.jpg'),

    (6, '스테인리스 조리 도구 세트',
     'https://www.coupang.com/vp/products/example6',
     '국자, 뒤집개, 집게 등 기본 조리 도구가 모두 포함된 세트입니다. 내구성이 좋은 스테인리스 재질입니다.',
     'https://via.placeholder.com/300x300?text=Cooking+Tools');
