-- ------------------------------------------------------------
-- Starter schema for MySQL 8.0
-- charset / engine
-- ------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS starterpack
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE starterpack;

-- ------------------------------------------------------------
-- 1) 멤버 (Member)
-- ------------------------------------------------------------
CREATE TABLE member (
  user_id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  email             VARCHAR(100)    NOT NULL,
  password          VARCHAR(255)    NULL,
  name              VARCHAR(50)     NOT NULL,
  provider          VARCHAR(20)     NULL,
  provider_id       VARCHAR(100)    NULL,
  profile_image_url VARCHAR(500)    NULL,
  is_active         BOOLEAN         NOT NULL DEFAULT TRUE,
  created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_member_email (email),
  UNIQUE KEY uk_member_provider_id (provider, provider_id),
  KEY idx_member_provider (provider),
  KEY idx_member_is_active (is_active)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2) 카테고리 (Category)
-- ------------------------------------------------------------
CREATE TABLE category (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name        VARCHAR(100)    NOT NULL,
  src         VARCHAR(500)    NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_category_name (name)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2) 상품 (Product)
-- ------------------------------------------------------------
CREATE TABLE product (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name          VARCHAR(200)    NOT NULL,
  link          VARCHAR(500)    NULL,
  product_type  VARCHAR(50)     NULL,     -- ERD: type
  src           VARCHAR(500)    NULL,
  cost          INT UNSIGNED    NULL,     -- ERD: cost
  like_count    INT UNSIGNED    NOT NULL DEFAULT 0,  -- ERD: like
  category_id   BIGINT UNSIGNED NULL,     -- ERD: category
  PRIMARY KEY (id),
  KEY idx_product_name (name),
  KEY idx_product_category (category_id),
  CONSTRAINT fk_product_category
    FOREIGN KEY (category_id)
    REFERENCES category(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT chk_product_cost_nonneg CHECK (cost IS NULL OR cost >= 0),
  CONSTRAINT chk_product_like_nonneg CHECK (like_count >= 0)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3) 스타터팩 (Pack)
-- ------------------------------------------------------------
CREATE TABLE pack (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name             VARCHAR(100)    NOT NULL,
  category_id      BIGINT UNSIGNED NULL,
  total_cost       INT UNSIGNED    NULL,
  pack_like_count  INT UNSIGNED    NOT NULL DEFAULT 0,
  src              VARCHAR(500)    NULL,
  description      TEXT            NULL,
  PRIMARY KEY (id),
  KEY idx_pack_category (category_id),
  CONSTRAINT fk_pack_category
    FOREIGN KEY (category_id)
    REFERENCES category(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT chk_pack_total_cost_nonneg CHECK (total_cost IS NULL OR total_cost >= 0),
  CONSTRAINT chk_pack_like_nonneg CHECK (pack_like_count >= 0)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 4) 상품_스타터팩 매핑 (PtoP)
--    복합 PK + FK, 부모 삭제 시 매핑도 함께 삭제
-- ------------------------------------------------------------
CREATE TABLE pack_product (
  pack_id     BIGINT UNSIGNED NOT NULL,
  product_id  BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (pack_id, product_id),
  KEY idx_pp_product (product_id),
  CONSTRAINT fk_pp_pack
    FOREIGN KEY (pack_id)
    REFERENCES pack(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_pp_product
    FOREIGN KEY (product_id)
    REFERENCES product(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- (옵션) 조회 최적화용 인덱스 예시.
-- ------------------------------------------------------------
-- 좋아요 순 상품/팩 랭킹
CREATE INDEX idx_product_like ON product(like_count DESC);
CREATE INDEX idx_pack_like ON pack(pack_like_count DESC);

-- 카테고리별 비용/정렬 조회가 많다면
CREATE INDEX idx_product_category_cost ON product(category_id, cost);
CREATE INDEX idx_pack_category_cost ON pack(category_id, total_cost);
