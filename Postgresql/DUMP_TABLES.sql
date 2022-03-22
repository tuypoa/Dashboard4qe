/*
DROP TABLE qeleitura;
DROP TABLE jarleitura;
DROP TABLE psaux;
DROP TABLE qeinfoiteration;
DROP TABLE qeinfoscf;
DROP TABLE qeresumo;
DROP TABLE comando;
DROP TABLE maquina_qearquivoin;
DROP TABLE maquina;
DROP TABLE qearquivoin;
DROP TABLE molecula;
*/

CREATE TABLE maquina (
  codigo serial NOT NULL PRIMARY KEY ,
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  nome varchar(50) NOT NULL,
  ssh varchar(100),
  senha varchar(20),
  rootpath varchar(150) NOT NULL,
  jarpath varchar(150) NOT NULL,  
  mincpu integer NOT NULL,
  maxcpu integer NOT NULL,
  cpuused numeric(5,2),
  memused numeric(5,2),
  ultimoacesso timestamp,  
  iniciarjob boolean NOT NULL DEFAULT TRUE,
  online boolean NOT NULL DEFAULT FALSE,
  ignorar boolean NOT NULL DEFAULT FALSE
);

CREATE TABLE jarleitura (
  codigo serial NOT NULL PRIMARY KEY,
  maquina_codigo int not null REFERENCES maquina (codigo),
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cpuused numeric(5,2),
  memused numeric(5,2)
);
 
CREATE TABLE molecula (
  codigo serial NOT NULL PRIMARY KEY,
  datahora timestamp NOT NULL DEFAULT current_timestamp,
  nome varCHAR(50) NOT NULL
);

CREATE TABLE qearquivoin (
  codigo serial NOT NULL PRIMARY KEY,
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hash character(32) NOT NULL UNIQUE,
  nome varchar(150) NOT NULL,
  descricao varchar(50),
  conteudo text NOT NULL,
  molecula_codigo integer NOT NULL REFERENCES molecula (codigo)
);

CREATE TABLE qeresumo (
  codigo serial NOT NULL PRIMARY KEY,
  qearquivoin_codigo int not null REFERENCES qearquivoin (codigo),
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hashoutput character(32) NOT NULL,
  processar boolean NOT NULL DEFAULT TRUE,
  nome varchar(150) NOT NULL,
  tamanhokb real NOT NULL,
  qtdecpu smallint NOT NULL,
  ultimalida timestamp,
  concluido boolean NOT NULL DEFAULT FALSE,
  executando boolean NOT NULL DEFAULT TRUE
);

CREATE TABLE qeleitura (
  codigo serial NOT NULL PRIMARY KEY,
  jarleitura_codigo int not null REFERENCES jarleitura (codigo),
  qeresumo_codigo int not null REFERENCES qeresumo (codigo),
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tamanhokb real NOT NULL
);

CREATE TABLE qeinfoscf (
  qeresumo_codigo int NOT NULL REFERENCES qeresumo (codigo),
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
  PRIMARY KEY (qeresumo_codigo, scfcycles)
);

CREATE TABLE qeinfoiteration (
  qeresumo_codigo int NOT NULL REFERENCES qeresumo (codigo),
  scfcycles smallint NOT NULL,
  iteration smallint NOT NULL,  
  cputime real not null,
  PRIMARY KEY (qeresumo_codigo, scfcycles, iteration)
);

CREATE TABLE maquina_qearquivoin (
  maquina_codigo integer not null REFERENCES maquina (codigo),
  qearquivoin_codigo integer not null REFERENCES qearquivoin (codigo),
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  path character varying(250) NOT NULL,	  
  nomein character varying(100) NOT NULL,
  nomeout character varying(100),	  
  ordem int NOT NULL,
  ignorar boolean NOT NULL DEFAULT FALSE,
  PRIMARY KEY (maquina_codigo, qearquivoin_codigo)
);

CREATE TABLE comando (
  codigo serial NOT NULL PRIMARY KEY,
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cmdtemplate varchar(100) NOT NULL,
  cmdprefixo varchar(30) NOT NULL
);

CREATE TABLE psaux (
  maquina_codigo integer NOT NULL REFERENCES maquina (codigo),
  comando_codigo integer NOT NULL REFERENCES comando (codigo),  
  pid int NOT NULL,  
  datahora timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  uid varchar(50) NOT NULL,
  qearquivoin_codigo integer REFERENCES qearquivoin (codigo),
  qeresumo_codigo integer REFERENCES qeresumo (codigo),
  cpu numeric(5,2) NOT NULL,
  mem numeric(5,2) NOT NULL,
  conteudo varchar(150) NOT NULL,
  PRIMARY KEY (maquina_codigo, comando_codigo, pid)
);


