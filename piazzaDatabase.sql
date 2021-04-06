/* CREATING DATABASE AND TABLES*/

CREATE DATABASE Piazza;

CREATE TABLE User(
    Email VARCHAR(50) NOT NULL,
    UserPassword VARCHAR(20) NOT NULL,
    PersonalName VARCHAR(100),
    CONSTRAINT User_PK PRIMARY KEY (Email)
);

CREATE TABLE Course(
    CourseCode VARCHAR(10) NOT NULL, 
    Term VARCHAR(20) NOT NULL,
    CourseName VARCHAR(100),
    AllowAnonymous CHAR(1), /* Either Y for Yes or NULL (default) for No or some other binary relation */
    CONSTRAINT Course_PK PRIMARY KEY (CourseCode, Term)
);

CREATE TABLE ParticipatesInCourse(
    Email VARCHAR(50) NOT NULL,
    CourseCode VARCHAR(10) NOT NULL,
    Term VARCHAR(20) NOT NULL,
    UserType VARCHAR(10) NOT NULL, /* Either Student or Instructor */
    CONSTRAINT ParticipatesInCourse_PK PRIMARY KEY (Email, CourseCode, Term),
    CONSTRAINT ParticipatesInCourse_FK1 FOREIGN KEY (Email) REFERENCES User(Email)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT ParticipatesInCourse_FK2 FOREIGN KEY (CourseCode, Term) REFERENCES Course(CourseCode, Term)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Folder(
    FolderID INTEGER NOT NULL,
    FolderName VARCHAR(30),
    CourseCode VARCHAR(10) NOT NULL,
    Term VARCHAR(30) NOT NULL,
    ParentFolderID INTEGER, /* FolderID of parent Folder if subfolder, NULL if root folder*/
    CONSTRAINT Folder_PK PRIMARY KEY (FolderID),
    CONSTRAINT Folder_FK1 FOREIGN KEY (CourseCode, Term) REFERENCES Course (CourseCode, Term)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT Folder_FK2 FOREIGN KEY (ParentFolderID) REFERENCES Folder(FolderID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Post(
    PostID INTEGER NOT NULL,
    Author VARCHAR(50) NOT NULL,
    PostText VARCHAR(500),
    ColorCode VARCHAR(20),
    FolderID INTEGER NOT NULL,
    OriginalPostID INTEGER, /* Can be NULL if first post in thread*/
    CONSTRAINT Post_PK PRIMARY KEY (PostID),
    CONSTRAINT Post_FK1 FOREIGN KEY (Author) REFERENCES User(Email)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT Post_FK2 FOREIGN KEY (FolderID) REFERENCES Folder(FolderID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT Post_FK3 FOREIGN KEY (OriginalPostID) REFERENCES Post(PostID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Tags(
    TagName VARCHAR(25) NOT NULL,
    CONSTRAINT Tags_PK PRIMARY KEY (TagName)
);

CREATE TABLE PostTags(
    PostID INTEGER NOT NULL,
    TagName VARCHAR(25) NOT NULL, /* A post is only in this table if it has one or more tags*/
    CONSTRAINT PostTags_PK PRIMARY KEY (PostID, TagName),
    CONSTRAINT PostTags_FK1 FOREIGN KEY (PostID) REFERENCES Post(PostID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT PostTags_FK2 FOREIGN KEY (TagName) REFERENCES Tags(TagName)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE ReplyToPost(
    PostID INTEGER NOT NULL,
    OriginalPostID INTEGER NOT NULL, /* Not part of this table if it is not a reply*/
    CONSTRAINT ReplyToPost_PK PRIMARY KEY (PostID, OriginalPostID),
    CONSTRAINT ReplyToPost_FK1 FOREIGN KEY (PostID) REFERENCES Post(PostID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT ReplyToPost_FK2 FOREIGN KEY (OriginalPostID) REFERENCES Post(PostID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE LinkedPosts(
    PostID INTEGER NOT NULL,
    LinkedPostID INTEGER NOT NULL,
    CONSTRAINT LinkedPosts_PK PRIMARY KEY (PostID, LinkedPostID),
    CONSTRAINT LinkedPosts_FK1 FOREIGN KEY (PostID) REFERENCES Post(PostID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT LinkedPosts_FK2 FOREIGN KEY (LinkedPostID) REFERENCES Post(PostID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE ReadPost(
    PostID INTEGER NOT NULL,
    Email VARCHAR(50) NOT NULL,
    CONSTRAINT ReadPost_PK PRIMARY KEY (PostID, Email),
    CONSTRAINT ReadPost_FK1 FOREIGN KEY (PostID) REFERENCES Post(PostID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT ReadPost_FK2 FOREIGN KEY (Email) REFERENCES User(Email)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE LikePost(
    PostID INTEGER NOT NULL,
    Email VARCHAR(50) NOT NULL,
    CONSTRAINT LikePost_PK PRIMARY KEY (PostID, Email),
    CONSTRAINT LikePost_FK1 FOREIGN KEY (PostID) REFERENCES Post(PostID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT LikePost_FK2 FOREIGN KEY (Email) REFERENCES User(Email)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

/* INSERTING VALUES INTO TABLES */

/* Creating users */
INSERT INTO User VALUES ("ingrikol@stud.ntnu.no", "testPassword1", "Ingrid");
INSERT INTO User VALUES ("lenakh@stud.ntnu.no", "testPassword2", "Lena");
INSERT INTO User VALUES ("ingvilcf@stud.ntnu.no", "testPassword3", "Ingvild");
INSERT INTO User VALUES ("espenmn@stud.ntnu.no", "testPassword4", "Espen");
INSERT INTO User VALUES ("sveinbra@ntnu.no", "testPassword5", "Svein Erik");

/* Creating courses */
INSERT INTO Course VALUES ("TDT4145", "Spring 2021", "Datamodellering og databasesystemer", "Y");
INSERT INTO Course VALUES ("TDT4120", "Fall 2020", "Algoritmer og datastrukturer", "Y");

/* Creating relation ParticipatesInCourse */
INSERT INTO ParticipatesInCourse VALUES ("ingrikol@stud.ntnu.no", "TDT4145", "Spring 2021", "Student");
INSERT INTO ParticipatesInCourse VALUES ("lenakh@stud.ntnu.no", "TDT4145", "Spring 2021", "Student");
INSERT INTO ParticipatesInCourse VALUES ("ingvilcf@stud.ntnu.no", "TDT4145", "Spring 2021", "Student");
INSERT INTO ParticipatesInCourse VALUES ("espenmn@stud.ntnu.no", "TDT4145", "Spring 2021", "Instructor");
INSERT INTO ParticipatesInCourse VALUES ("sveinbra@ntnu.no", "TDT4145", "Spring 2021", "Instructor");
INSERT INTO ParticipatesInCourse VALUES ("ingrikol@stud.ntnu.no", "TDT4120", "Fall 2020", "Student");
INSERT INTO ParticipatesInCourse VALUES ("lenakh@stud.ntnu.no", "TDT4120", "Fall 2020", "Student");
INSERT INTO ParticipatesInCourse VALUES ("ingvilcf@stud.ntnu.no", "TDT4120", "Fall 2020", "Student");

/* Creating Folders */
INSERT INTO Folder VALUES (1, "Project", "TDT4145", "Spring 2021", NULL);
INSERT INTO Folder VALUES (2, "Exercises", "TDT4145", "Spring 2021", NULL);
INSERT INTO Folder VALUES (21, "Exercise 1", "TDT4145", "Spring 2021", 2);
INSERT INTO Folder VALUES (3, "Exam", "TDT4145", "Spring 2021", NULL);

/* Creating Post */
INSERT INTO Post VALUES(1, "lenakh@stud.ntnu.no", "Hei, jeg heter Lena.", "blue", 1, NULL);
INSERT INTO Post VALUES(2, "ingvilcf@stud.ntnu.no", "Hei Lena :)", "dark blue", 1, 1);
INSERT INTO Post VALUES(3, "sveinbra@ntnu.no", "Info om eksamen...", "red", 3, NULL);

/*Creating Tags*/
INSERT INTO Tags VALUES("Question");
INSERT INTO Tags VALUES("Home");
INSERT INTO Tags VALUES("Announcement");
INSERT INTO Tags VALUES("Homework");
INSERT INTO Tags VALUES("Homework Solutions");
INSERT INTO Tags VALUES("Lecture Notes");
INSERT INTO Tags VALUES("General Announcements");

/*Tag posts*/
INSERT INTO PostTags VALUES(1, "Home");
INSERT INTO PostTags VALUES(3, "General Announcements");

/*Creating ReadPost*/
INSERT INTO ReadPost VALUES("1", "lenakh@stud.ntnu.no");
INSERT INTO ReadPost VALUES("1", "ingrikol@stud.ntnu.no");
INSERT INTO ReadPost VALUES("2", "Lenakh@stud.ntnu.no");
INSERT INTO ReadPost VALUES("1", "ingvilcf@stud.ntnu.no");
