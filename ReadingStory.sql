--
-- File generated with SQLiteStudio v3.4.17 on Sun Mar 9 22:20:00 2025
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: Bookmark
DROP TABLE IF EXISTS Bookmark;

CREATE TABLE IF NOT EXISTS Bookmark (
    UserId    INTEGER  NOT NULL,
    StoryId   INTEGER  NOT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (
        UserId,
        StoryId
    ),
    FOREIGN KEY (
        UserId
    )
    REFERENCES User (UserId),
    FOREIGN KEY (
        StoryId
    )
    REFERENCES Story (StoryId) 
);

INSERT INTO Bookmark (
                         UserId,
                         StoryId,
                         CreatedAt
                     )
                     VALUES (
                         2,
                         1,
                         '2025-03-04 03:41:53'
                     );

INSERT INTO Bookmark (
                         UserId,
                         StoryId,
                         CreatedAt
                     )
                     VALUES (
                         3,
                         2,
                         '2025-03-04 03:41:53'
                     );


-- Table: Chapter
DROP TABLE IF EXISTS Chapter;

CREATE TABLE IF NOT EXISTS Chapter (
    ChapterId INTEGER  PRIMARY KEY AUTOINCREMENT,
    StoryId   INTEGER  NOT NULL,
    Title     TEXT     NOT NULL,
    Content   TEXT     NOT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (
        StoryId
    )
    REFERENCES Story (StoryId) 
);

INSERT INTO Chapter (
                        ChapterId,
                        StoryId,
                        Title,
                        Content,
                        CreatedAt,
                        UpdatedAt
                    )
                    VALUES (
                        1,
                        1,
                        'Chapter 1-1',
                        'Content of chapter 1-1',
                        '2025-03-04 03:41:53',
                        '2025-03-04 03:41:53'
                    );

INSERT INTO Chapter (
                        ChapterId,
                        StoryId,
                        Title,
                        Content,
                        CreatedAt,
                        UpdatedAt
                    )
                    VALUES (
                        2,
                        1,
                        'Chapter 1-2',
                        'Content of chapter 1-2',
                        '2025-03-04 03:41:53',
                        '2025-03-04 03:41:53'
                    );


-- Table: Comment
DROP TABLE IF EXISTS Comment;

CREATE TABLE IF NOT EXISTS Comment (
    CommentId       INTEGER  PRIMARY KEY AUTOINCREMENT,
    UserId          INTEGER  NOT NULL,
    ChapterId       INTEGER,
    StoryId         INTEGER,
    ParentCommentId INTEGER,
    Content         TEXT     NOT NULL,
    CreatedAt       DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (
        UserId
    )
    REFERENCES User (UserId),
    FOREIGN KEY (
        StoryId
    )
    REFERENCES Story (StoryId),
    FOREIGN KEY (
        ChapterId
    )
    REFERENCES Chapter (ChapterId),
    FOREIGN KEY (
        ParentCommentId
    )
    REFERENCES Comment (CommentId) 
);

INSERT INTO Comment (
                        CommentId,
                        UserId,
                        ChapterId,
                        StoryId,
                        ParentCommentId,
                        Content,
                        CreatedAt,
                        UpdatedAt
                    )
                    VALUES (
                        1,
                        2,
                        NULL,
                        1,
                        NULL,
                        'Great story!',
                        '2025-03-04 03:41:53',
                        '2025-03-04 03:41:53'
                    );

INSERT INTO Comment (
                        CommentId,
                        UserId,
                        ChapterId,
                        StoryId,
                        ParentCommentId,
                        Content,
                        CreatedAt,
                        UpdatedAt
                    )
                    VALUES (
                        2,
                        3,
                        1,
                        NULL,
                        NULL,
                        'Nice first chapter!',
                        '2025-03-04 03:41:53',
                        '2025-03-04 03:41:53'
                    );


-- Table: Follower
DROP TABLE IF EXISTS Follower;

CREATE TABLE IF NOT EXISTS Follower (
    FollowerId  INTEGER  NOT NULL,
    FollowingId INTEGER  NOT NULL,
    CreatedAt   DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (
        FollowerId,
        FollowingId
    ),
    FOREIGN KEY (
        FollowerId
    )
    REFERENCES User (UserId),
    FOREIGN KEY (
        FollowingId
    )
    REFERENCES User (UserId) 
);


-- Table: Genre
DROP TABLE IF EXISTS Genre;

CREATE TABLE IF NOT EXISTS Genre (
    GenreId     INTEGER PRIMARY KEY AUTOINCREMENT,
    Name        TEXT    UNIQUE
                        NOT NULL,
    Description TEXT
);

INSERT INTO Genre (
                      GenreId,
                      Name,
                      Description
                  )
                  VALUES (
                      1,
                      'Fantasy',
                      'Fantasy stories'
                  );

INSERT INTO Genre (
                      GenreId,
                      Name,
                      Description
                  )
                  VALUES (
                      2,
                      'Romance',
                      'Romantic stories'
                  );


-- Table: Notification
DROP TABLE IF EXISTS Notification;

CREATE TABLE IF NOT EXISTS Notification (
    NotificationId INTEGER  PRIMARY KEY AUTOINCREMENT,
    UserId         INTEGER  NOT NULL,
    Type           TEXT     NOT NULL,
    Content        TEXT     NOT NULL,
    ImageLeft      TEXT     NOT NULL,
    StoryId        INTEGER,
    ChapterId      INTEGER,
    CommentId      INTEGER,
    CreatedAt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (
        UserId
    )
    REFERENCES User (UserId),
    FOREIGN KEY (
        StoryId
    )
    REFERENCES Story (StoryId),
    FOREIGN KEY (
        ChapterId
    )
    REFERENCES Chapter (ChapterId),
    FOREIGN KEY (
        CommentId
    )
    REFERENCES Comment (CommentId) 
);


-- Table: ReadingHistory
DROP TABLE IF EXISTS ReadingHistory;

CREATE TABLE IF NOT EXISTS ReadingHistory (
    UserId     INTEGER  NOT NULL,
    StoryId    INTEGER  NOT NULL,
    ChapterId  INTEGER  NOT NULL,
    [Like]     INTEGER  DEFAULT 0,-- 0 = chua like, 1 = dã like
    View       INTEGER  DEFAULT 0,
    LastReadAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (
        UserId,
        StoryId,
        ChapterId
    ),
    FOREIGN KEY (
        UserId
    )
    REFERENCES User (UserId),
    FOREIGN KEY (
        StoryId
    )
    REFERENCES Story (StoryId),
    FOREIGN KEY (
        ChapterId
    )
    REFERENCES Chapter (ChapterId) 
);

INSERT INTO ReadingHistory (
                               UserId,
                               StoryId,
                               ChapterId,
                               [Like],
                               View,
                               LastReadAt
                           )
                           VALUES (
                               2,
                               1,
                               1,
                               1,
                               1,
                               '2025-03-04 03:41:53'
                           );

INSERT INTO ReadingHistory (
                               UserId,
                               StoryId,
                               ChapterId,
                               [Like],
                               View,
                               LastReadAt
                           )
                           VALUES (
                               3,
                               2,
                               1,
                               0,
                               1,
                               '2025-03-04 03:41:53'
                           );

INSERT INTO ReadingHistory (
                               UserId,
                               StoryId,
                               ChapterId,
                               [Like],
                               View,
                               LastReadAt
                           )
                           VALUES (
                               1,
                               1,
                               1,
                               1,
                               1,
                               '2025-03-04 03:41:59'
                           );


-- Table: Story
DROP TABLE IF EXISTS Story;

CREATE TABLE IF NOT EXISTS Story (
    StoryId       INTEGER  PRIMARY KEY AUTOINCREMENT,
    AuthorId      INTEGER  NOT NULL,
    Title         TEXT     NOT NULL,
    Description   TEXT,
    CoverImageUrl TEXT,
    GenreId       INTEGER  NOT NULL,
    Status        TEXT     DEFAULT 'ongoing',
    CreatedAt     DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (
        AuthorId
    )
    REFERENCES User (UserId),
    FOREIGN KEY (
        GenreId
    )
    REFERENCES Genre (GenreId) 
);

INSERT INTO Story (
                      StoryId,
                      AuthorId,
                      Title,
                      Description,
                      CoverImageUrl,
                      GenreId,
                      Status,
                      CreatedAt,
                      UpdatedAt
                  )
                  VALUES (
                      1,
                      2,
                      'Story 1',
                      'Description of Story 1',
                      'cover1.jpg',
                      1,
                      'ongoing',
                      '2025-03-04 03:41:53',
                      '2025-03-04 03:41:53'
                  );

INSERT INTO Story (
                      StoryId,
                      AuthorId,
                      Title,
                      Description,
                      CoverImageUrl,
                      GenreId,
                      Status,
                      CreatedAt,
                      UpdatedAt
                  )
                  VALUES (
                      2,
                      2,
                      'Story 2',
                      'Description of Story 2',
                      'cover2.jpg',
                      2,
                      'completed',
                      '2025-03-04 03:41:53',
                      '2025-03-04 03:41:53'
                  );


-- Table: StoryTag
DROP TABLE IF EXISTS StoryTag;

CREATE TABLE IF NOT EXISTS StoryTag (
    StoryTagId INTEGER PRIMARY KEY AUTOINCREMENT,
    Name       TEXT    UNIQUE
                       NOT NULL
);


-- Table: StoryTagMapping
DROP TABLE IF EXISTS StoryTagMapping;

CREATE TABLE IF NOT EXISTS StoryTagMapping (
    StoryId    INTEGER NOT NULL,
    StoryTagId INTEGER NOT NULL,
    PRIMARY KEY (
        StoryId,
        StoryTagId
    ),
    FOREIGN KEY (
        StoryId
    )
    REFERENCES Story (StoryId),
    FOREIGN KEY (
        StoryTagId
    )
    REFERENCES StoryTag (StoryTagId) 
);


-- Table: User
DROP TABLE IF EXISTS User;

CREATE TABLE IF NOT EXISTS User (
    UserId       INTEGER  PRIMARY KEY AUTOINCREMENT,
    Username     TEXT     NOT NULL,
    Email        TEXT     UNIQUE
                          NOT NULL,
    PasswordHash TEXT     NOT NULL,
    AvatarUrl    TEXT,
    Bio          TEXT,
    Role         TEXT     DEFAULT 'user',
    CreatedAt    DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt    DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO User (
                     UserId,
                     Username,
                     Email,
                     PasswordHash,
                     AvatarUrl,
                     Bio,
                     Role,
                     CreatedAt,
                     UpdatedAt
                 )
                 VALUES (
                     1,
                     'admin1',
                     'admin1@example.com',
                     'hashed_password',
                     NULL,
                     'Admin user',
                     'admin',
                     '2025-03-04 03:41:53',
                     '2025-03-04 03:41:53'
                 );

INSERT INTO User (
                     UserId,
                     Username,
                     Email,
                     PasswordHash,
                     AvatarUrl,
                     Bio,
                     Role,
                     CreatedAt,
                     UpdatedAt
                 )
                 VALUES (
                     2,
                     'user1',
                     'user1@example.com',
                     'hashed_password',
                     'avatar1.jpg',
                     'Bio of user1',
                     'user',
                     '2025-03-04 03:41:53',
                     '2025-03-04 03:41:53'
                 );

INSERT INTO User (
                     UserId,
                     Username,
                     Email,
                     PasswordHash,
                     AvatarUrl,
                     Bio,
                     Role,
                     CreatedAt,
                     UpdatedAt
                 )
                 VALUES (
                     3,
                     'user2',
                     'user2@example.com',
                     'hashed_password',
                     'avatar2.jpg',
                     'Bio of user2',
                     'user',
                     '2025-03-04 03:41:53',
                     '2025-03-04 03:41:53'
                 );

INSERT INTO User (
                     UserId,
                     Username,
                     Email,
                     PasswordHash,
                     AvatarUrl,
                     Bio,
                     Role,
                     CreatedAt,
                     UpdatedAt
                 )
                 VALUES (
                     4,
                     'tung',
                     'tung@gmail.com',
                     '12345678',
                     NULL,
                     NULL,
                     'user',
                     '2025-03-08 17:21:45',
                     '2025-03-08 17:21:45'
                 );


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
