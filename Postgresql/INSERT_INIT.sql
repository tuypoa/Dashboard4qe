

INSERT INTO molecula(nome) values ('Macitentan'), ('Ritonavir');

INSERT INTO maquina(nome,rootpath,jarpath,ssh,senha,mincpu,maxcpu,ignorar) values
	('Maq1 Ubuntu10',
		'/home/ppgcf-joao/Desktop/Guilherme/DrugDesign-Coformers/',
		'/home/ppgcf-joao/Desktop/Guilherme/Dashboard4qe_v0.1.jar',
		'ppgcf-joao@192.168.0.106','labnovo2018',8,8, false),
	('Maq2 Ubuntu10',
		'/home/lightningbolt/Desktop/Guilherme/',
		'/home/lightningbolt/Desktop/Guilherme/Dashboard4qe_v0.1.jar',
		'lightningbolt@192.168.0.100','labnovo2018',8,8, false),
	('Maq3 Debian11',
		'/home/debian01/Documents/Guilherme/DrugDesign-Coformers/',
		'/home/debian01/Documents/Guilherme/Dashboard4qe_v0.1.jar',
		'debian01@192.168.0.105','labnovo2018',12,24, false),
	('Maq4 Ubuntu20',
		'/home/lapfarsc/Desktop/Guilherme/DrugDesign-Coformers/',
		'/home/lapfarsc/Desktop/Guilherme/Dashboard4qe_v0.1.jar',
		'lapfarsc@192.168.0.108','labnovo2018',8,16, false),
	('Maq5 Ubuntu20',
		'/home/lapfarsc/Desktop/Guilherme/DrugDesign-Coformers/',
		'/home/lapfarsc/Desktop/Guilherme/Dashboard4qe_v0.1.jar',
		'lapfarsc@192.168.0.110','labnovo2018',8,16, true);

INSERT INTO comando(cmdtemplate,cmdprefixo) values
	('java -jar @JARPATH @ARG &','java'),
	('mpirun -np @NCPU pw.x -in @QEARQIN > @QEARQOUT &','mpirun'),
	('pw.x -in @QEARQIN > @QEARQOUT &','pw.x');

