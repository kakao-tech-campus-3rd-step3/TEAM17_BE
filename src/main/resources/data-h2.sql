-- ------------------------------------------------------------
INSERT INTO member (email, password, name, nickname, is_active, role, provider)
VALUES
('admin@test.com', '$2a$10$jznYgTod3Yf2cjYfwFgrRe7CGo7Ubx0hBwyYBi1046U.ZN.GTGm16', '관리자', '관리자1234567', TRUE, 'ADMIN', 'EMAIL'),
('user@test.com',  '{noop}pass', '일반사용자', '일반사용자7654321', TRUE, 'USER', 'EMAIL');

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
-- 2) 상품 (Product) Mock 데이터 (각 카테고리 별 2개씩)
INSERT INTO product (name, link, product_type, src, cost, like_count, category_id) VALUES
-- 베이킹
('스텐 원형 베이킹 채망', 'https://www.coupang.com/vp/products/6164239115?itemId=11983535216&vendorItemId=79256004237&q=%EB%B2%A0%EC%9D%B4%ED%82%B9&searchId=f9526dd91103163&sourceType=search&itemsCount=36&searchRank=6&rank=6', '채망', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/0b2d/93ee6787be53cb1d540f561a8243c7cf1b295e103c746fc3878300a8a93f.jpg', 4900, 1, 1),
('컨벤셔널 오븐', 'https://www.coupang.com/vp/products/6511908960?itemId=14387146729&vendorItemId=81631429101&pickType=COU_PICK&q=%EC%98%A4%EB%B8%90&searchId=5c9aa9b45918430&sourceType=search&itemsCount=36&searchRank=1&rank=1', '오븐', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/1025_amir_coupang_oct_80k/677a/13e2bb7a02934df34d38b2cfbda406c7f9befb761164be76e6ea3b5e2b2a.jpg', 59000, 1, 1),
-- 캠핑
('LED 감성 캠핑 랜턴', 'https://www.coupang.com/vp/products/8512052933?itemId=24639798903&vendorItemId=91650673932&q=%EC%BA%A0%ED%95%91&searchId=38a72593684652&sourceType=search&itemsCount=36&searchRank=3&rank=3', '랜턴', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/137506773661373-2113f02f-ea77-43c5-88e1-3d890fb5c4cb.jpg', 7930, 10, 2),
('국산 참나무 장작', 'https://www.coupang.com/vp/products/5381058739?itemId=17052556988&vendorItemId=84226983297&q=%EC%BA%A0%ED%95%91&searchId=6734257d2205198&sourceType=search&itemsCount=36&searchRank=3&rank=3', '장작', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/1025_amir_coupang_oct_80k/bd16/8bc52d34430e208b53837946cd11eabdc1ce6bd6c9c69347a331142f820d.jpg', 10790, 10, 2),
-- 러닝
('써머텍트 기능성 헤어밴드', 'https://www.coupang.com/vp/products/5716566331?itemId=9548023757&vendorItemId=84508313637&pickType=COU_PICK&q=%EB%9F%B0%EB%8B%9D%EC%9A%A9%ED%92%88&searchId=281cb41f1784105&sourceType=search&itemsCount=36&searchRank=2&rank=2', '헤어밴드', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/248528520729619-fa9a4de4-adcc-4c27-a736-510cac393f0b.png', 10630, 15, 3),
('노바핏 러닝 벨트 가방', 'https://www.coupang.com/vp/products/8625423187?itemId=25029918527&vendorItemId=92034744614&q=%EB%9F%B0%EB%8B%9D%EC%9A%A9%ED%92%88&searchId=281cb41f1784105&sourceType=search&itemsCount=36&searchRank=4&rank=4', '런닝 가방', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/f91e/cbe73c27fc560bc593ac5221ff3a7be6cf84585fadbf76305632aaa8cf38.png', 20970, 10, 3),
-- 독서
('워너디스 반투명 책갈피', 'https://www.coupang.com/vp/products/7163840572?itemId=22379594270&vendorItemId=89424578387&q=%EB%8F%85%EC%84%9C&searchId=1c50858f2411889&sourceType=search&itemsCount=36&searchRank=3&rank=3', '책갈피', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/3294/284d2729b6d13f20807723e72a14ba08540c0b5a39f84889750fca2a4860.jpg', 5400, 10, 4),
('코믈리 투명 독서대', 'https://www.coupang.com/vp/products/7187172452?itemId=18138407570&vendorItemId=85294721324&pickType=COU_PICK&q=%EB%8F%85%EC%84%9C%EB%8C%80&searchId=bfacb4853198297&sourceType=search&itemsCount=36&searchRank=3&rank=3', '독서대', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/36a4/73e4ff422545e2fbd0000cf829d102fbf81daf2d95709107972d0a8a5ae2.jpg', 28500, 150, 4),
-- 헬스
('제로투히어로 헬스 스트랩', 'https://www.coupang.com/vp/products/2270488247?itemId=24741065401&vendorItemId=76864668317&pickType=COU_PICK&q=%ED%97%AC%EC%8A%A4+%EC%8A%A4%ED%8A%B8%EB%9E%A9&searchId=bbe21cf61286104&sourceType=search&itemsCount=36&searchRank=1&rank=1', '헬스 스트랩', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/1637211059741349-6cecca3f-64a8-41d3-8f4e-c0a0989ab638.jpg', 28400, 280, 5),
('제로투히어로 리프팅 벨트', 'https://www.coupang.com/vp/products/5569057581?itemId=6580963230&vendorItemId=73874960075&q=%ED%97%AC%EC%8A%A4%20%EB%B2%A8%ED%8A%B8&searchId=3ea9a25f3217067&sourceType=search&itemsCount=36&searchRank=1&rank=1', '리프팅 벨트', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/1025_amir_coupang_oct_80k/9e6e/7297d5bec77ca6093e8ef4bdecd0f4ae8319f8b8c406bad2d593f866c5b8.jpg', 69400, 210, 5),
-- 요리
('코멧 다이아몬드 코팅 프라이팬', 'https://www.coupang.com/vp/products/1419414761?itemId=2457160694&vendorItemId=70450694245&pickType=COU_PICK&q=%ED%94%84%EB%9D%BC%EC%9D%B8%ED%8C%AC&searchId=9b8a92bf1339361&sourceType=search&itemsCount=36&searchRank=1&rank=1', '프라이팬', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/517625530368447-19024890-d0c1-40b0-919f-8f257d50f130.jpg', 15190, 330, 6),
('코멧 걸이형 양면 도마', 'https://www.coupang.com/vp/products/7991722426?itemId=22215045691&vendorItemId=89261039055&pickType=COU_PICK&q=%EB%8F%84%EB%A7%88&searchId=a54d18fe1837517&sourceType=search&itemsCount=36&searchRank=1&rank=1', '도마', 'https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/2663855586682-dade83ce-1801-47df-8c99-bce4cca91a90.jpg', 8290, 260, 6);

-- ------------------------------------------------------------
-- 3) 스타터팩 (Pack) Mock 데이터 (6개)
INSERT INTO pack (name, category_id, total_cost, pack_like_count, src, description) VALUES
('홈베이킹 기본 스타터팩', 1, 100000, 80, 'https://i.namu.wiki/i/8MZZehLGZ1TCO4G7sBivu6GwEpFxajfYyXJ-m-2SIdrIH-4_1amvSyW-6fWykumnu0koFi6LZGNMJLV1O9k7sg.webp', '베이킹 기본 스타터팩 설명입니다'),
('캠핑 기본 스타터팩',     2, 200000, 150, 'https://blog-static.kkday.com/ko/blog/wp-content/uploads/korea_camping_spot_5.jpg', '캠핑 기본 스타터팩 설명입니다'),
('러닝 기본 스타터팩',     3, 333333, 210, 'https://cdn-icons-png.flaticon.com/512/4112/4112938.png', '런닝 기본 스타터팩 설명입니다'),
('독서 기본 스타터팩',     4, 444444, 110, 'https://img.khan.co.kr/lady/2020/04/20/l_2020042004000008300185672.jpg', '독서 기본 스타터팩 설명입니다'),
('헬스 기본 스타터팩',     5, 555555, 180, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0w-245t0fqP7arr89Qf-I6aEyO_6lBNZ_TA&s', '헬스 기본 스타터팩 설명입니다'),
('요리 기본 스타터팩',     6, 666666, 160, 'https://media.istockphoto.com/id/1081422898/ko/%EC%82%AC%EC%A7%84/%ED%8C%AC-%ED%8A%80%EA%B9%80-%EC%98%A4%EB%A6%AC.jpg?s=612x612&w=0&k=20&c=OZBRZbLLnwfUO9NNjwzkK03C4iwtFv4kmey3pHhrJwQ=', '요리 기본 스타터팩 설명입니다');

-- ------------------------------------------------------------
-- 4) 상품_스타터팩 매핑 (PtoP) Mock 데이터
INSERT INTO pack_product (pack_id, product_id) VALUES
(1, 1), (1, 2),
(2, 3), (2, 4),
(3, 5), (3, 6),
(4, 7), (4, 8),
(5, 9), (5, 10),
(6, 11), (6, 12);
