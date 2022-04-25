# Dashboard4qe
Centralized real-time convergence monitoring of multiple Quantum Espresso output files generated on multiple machines.


<b>INSTALL</b>
1. Install postgresql server following instructions: Postgresql/README.md
2. Install php server (apache2) and modules "php-pgsql" "php-gd"
3. Deploy JARs and check directories on db table "maquina"
4. Configure cron execution
<pre>
$ crontab -e
# m h  dom mon dow   command
*/15 * * * * java -jar /home/Desktop/Dashboard4qe_v0.1.jar HEAD
</pre>

