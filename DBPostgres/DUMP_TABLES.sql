-- DROP TABLE maquina;

CREATE TABLE maquina
(
  codigo serial NOT NULL,
  nome character varying(50) NOT NULL,
  path character varying(100) NOT NULL,
  ssh character varying(100),
  senha character varying(20),
  mincpu integer NOT NULL,
  maxcpu integer NOT NULL,
  CONSTRAINT maquina_pkey PRIMARY KEY (codigo)
)
