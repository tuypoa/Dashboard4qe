
1. Install postgresql and change de password

	root# apt-get install postgresql pgadmin3
	root$ su postgres
	postgres$ psql
		> ALTER ROLE 'postgres' WITH PASSWORD 'postgres';
		\q

2. Execute DUMP.sql on PgAdmin3 console

3. Change "firewall" config for postgresql

	root# gedit /etc/postgresql/9.3/main/pg_hba.conf
		#ADD LINE:
		host    all             all             all		        md5
	root# service postgresql restart

