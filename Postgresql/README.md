
1. Install postgresql and change the password
<pre>
	root# apt-get install postgresql
	root$ su postgres
	postgres$ psql
		> ALTER ROLE 'postgres' WITH PASSWORD 'postgres';
		\q
</pre>
2. Execute DUMP.sql

3. Change access config 
<pre>
	root# gedit /etc/postgresql/9.3/main/pg_hba.conf
		#ADD LINE:
		host    all             all             all		        md5
		
	root# gedit /etc/postgresql/9.3/main/postgresql.conf		
		#CHANGE LINE:
		*FROM:
		 #listen_addresses = '0.0.0.0'
		*TO: 
		 listen_addresses = '*'
		 
	root# service postgresql restart
</pre>
