CREATE TABLE category
(
    id    BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX category_title_idx ON category (title);

CREATE TABLE note
(
    id          BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    recipe_id   BIGINT
);
CREATE INDEX note_recipe_id_idx ON note (recipe_id);

CREATE TABLE recipe
(
    id          BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX recipe_title_idx ON recipe (title);

CREATE TABLE recipe_to_category
(
    recipe_id   BIGINT NOT NULL,
    category_id BIGINT NOT NULL
);
CREATE UNIQUE INDEX recipe_categories_idx ON recipe_to_category (recipe_id, category_id);
