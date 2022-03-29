# Dashboard4qe
Centralized real-time convergence monitoring of multiple Quantum Espresso output files generated on multiple machines.


<b>INSTALL</b>
1. Install postgresql server
	1.1 DUMP database tables
2. Install php server (apache2)
	2.1 Install php-pgsql and php-gd
3. Install JAR
4. Install cron 
<pre>
$ crontab -e
# m h  dom mon dow   command
*/15 * * * * java -jar /home/Desktop/Guilherme/Dashboard4qe_v0.1.jar HEAD
</pre>

