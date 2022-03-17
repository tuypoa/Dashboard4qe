/*
DROP TABLE psaux;
DROP TABLE infoscf;
DROP TABLE leitura;
DROP TABLE resumo;
DROP TABLE comando;
DROP TABLE maquina_arquivoin;
DROP TABLE maquina;
DROP TABLE arquivoin;
DROP TABLE molecula;

*/

CREATE TABLE maquina (
  codigo serial NOT NULL PRIMARY KEY ,
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  nome varchar(50) NOT NULL,
  rootpath varchar(100) NOT NULL,
  ssh varchar(100),
  senha varchar(20),
  mincpu integer NOT NULL,
  maxcpu integer NOT NULL,
  cpuused numeric(5,2),
  memused numeric(5,2),
  ultimoacesso timestamp,
  ultimalida timestamp
);
 
CREATE TABLE molecula (
  codigo serial NOT NULL PRIMARY KEY,
  datahora timestamp NOT NULL DEFAULT current_timestamp,
  nome varCHAR(50) NOT NULL
);

CREATE TABLE arquivoin (
  codigo serial NOT NULL PRIMARY KEY,
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hash character(32) NOT NULL UNIQUE,
  nome varchar(150) NOT NULL,
  conteudo text NOT NULL,
  molecula_codigo integer NOT NULL REFERENCES molecula (codigo)
);

CREATE TABLE resumo (
  codigo serial NOT NULL PRIMARY KEY,
  arquivoin_codigo int not null REFERENCES arquivoin (codigo),
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hash character(32) NOT NULL,
  nome varchar(150) NOT NULL,
  tamanho real NOT NULL,
  ultimalida timestamp,
  jobconcluido boolean NOT NULL DEFAULT FALSE,
  jobexecutando boolean NOT NULL DEFAULT FALSE
);

CREATE TABLE leitura (
  codigo serial NOT NULL PRIMARY KEY,
  resumo_codigo int not null REFERENCES resumo (codigo),
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tamanho real NOT NULL  
);

CREATE TABLE infoscf (
  resumo_codigo int NOT NULL REFERENCES resumo (codigo),
  scfcycles smallint NOT NULL,
  bfgssteps smallint,
  converged boolean,
  enthalpy real,
  volume real,
  density real,
  iterations smallint,
  cputime real,
  cellparams text,
  atomicpositions text,
  PRIMARY KEY (resumo_codigo, scfcycles)
);

CREATE TABLE infoiteration (
  resumo_codigo int NOT NULL REFERENCES resumo (codigo),
  scfcycles smallint NOT NULL,
  iteration smallint NOT NULL,  
  cputime real not null,
  PRIMARY KEY (resumo_codigo, scfcycles, iteration)
);

CREATE TABLE maquina_arquivoin (
  maquina_codigo integer not null REFERENCES maquina (codigo),
  arquivoin_codigo integer not null REFERENCES arquivoin (codigo),
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  path character varying(250) NOT NULL,	  
  nomein character varying(100) NOT NULL,
  nomeout character varying(100),	  
  ordem int NOT NULL,
  ignorar boolean NOT NULL DEFAULT FALSE,
  PRIMARY KEY (maquina_codigo, arquivoin_codigo)
);

CREATE TABLE comando (
  codigo serial NOT NULL PRIMARY KEY,
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cmdtemplate varchar(100) NOT NULL,
  cmdprefixo varchar(30) NOT NULL
);

CREATE TABLE psaux (
  maquina_codigo integer NOT NULL REFERENCES maquina (codigo),
  arquivoin_codigo integer NOT NULL REFERENCES arquivoin (codigo),
  comando_codigo integer NOT NULL REFERENCES comando (codigo),
  resumo_codigo integer REFERENCES resumo (codigo),
  pid int NOT NULL,
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  uid int NOT NULL,
  cpu numeric(5,2) NOT NULL,
  mem numeric(5,2) NOT NULL,
  PRIMARY KEY (maquina_codigo, arquivoin_codigo, comando_codigo, resumo_codigo, pid)
);

