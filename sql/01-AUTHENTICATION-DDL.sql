CREATE SCHEMA AUTHENTICATION;

CREATE SEQUENCE AUTHENTICATION.SQ_NM_ID_USER;

-- CCAA
CREATE TABLE AUTHENTICATION.CCAA (
    DE_CCAA_ID              CHAR (2),
    DE_CCAA_NAME            CHAR VARYING(100),
    CONSTRAINT PK_CCAA
        PRIMARY KEY (DE_CCAA_ID)
);

-- USER
CREATE TABLE AUTHENTICATION.USER (
	NM_USER_ID	     INTEGER DEFAULT nextval('AUTHENTICATION.SQ_NM_ID_USER'),
    DE_EMAIL         CHAR VARYING (255) NOT NULL,
    DE_PASSWORD      CHAR VARYING (255) NOT NULL,
	DE_NAME          CHAR VARYING (50) NOT NULL,
	DE_SUBNAME       CHAR VARYING (100),
	DE_PHONE         CHAR VARYING (15),
    FC_CREATION_DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FC_UPDATE_DATE   TIMESTAMP,
	DE_CCAA_ID       CHAR (2) NOT NULL,
    FC_LAST_LOGIN    TIMESTAMP,
    IN_ENABLED       BOOLEAN DEFAULT FALSE,
    CONSTRAINT PK_USER
        PRIMARY KEY (NM_USER_ID),
    CONSTRAINT UNQ_USER_EMAIL
        UNIQUE (DE_EMAIL),
	CONSTRAINT FK_USER_CCAA
        FOREIGN KEY (DE_CCAA_ID)
            REFERENCES AUTHENTICATION.CCAA (DE_CCAA_ID)
);

ALTER SEQUENCE AUTHENTICATION.SQ_NM_ID_USER
    OWNED BY AUTHENTICATION.USER.NM_USER_ID;

CREATE INDEX IN_AUTHENTICATION_USER_EMAIL
    ON AUTHENTICATION.USER(DE_EMAIL);

-- USER_ROLE
CREATE TABLE AUTHENTICATION.USER_ROLE (
    NM_USER_ID              INTEGER NOT NULL,
    DE_ROLE_ID              CHAR (1) NOT NULL,
    FC_CREATION_DATE        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_USER_ROLE
        PRIMARY KEY (NM_USER_ID, DE_ROLE_ID),
    CONSTRAINT FK_USER_ROLE_USER
        FOREIGN KEY (NM_USER_ID)
            REFERENCES AUTHENTICATION.USER (NM_USER_ID)
);
