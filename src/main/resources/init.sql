CREATE TABLE Issue (
    number INT,
    id INT,
    projectName VARCHAR(255),
    title VARCHAR(255),
    body TEXT,
    owner VARCHAR(255),
    createdAt DATETIME,
    updatedAt DATETIME,
    type VARCHAR(255),
    state VARCHAR(255),
    refUrl VARCHAR(255)
);
