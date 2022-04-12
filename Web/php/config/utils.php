<?php

function definirNomeSistemaCristal($cellparams){
    echo "<!-- \n ".$cellparams." -->";
    $linhas = explode("\n",$cellparams);
    if(sizeof($linhas)<4){
        echo "-";
    }else{
        $cell_a = $linhas[1];
        $cell_b = $linhas[2];
        $cell_c = $linhas[3];
        do {
            $cell_a = str_replace("  ", " ", trim($cell_a), $count);
        } while ($count > 0);
        do {
            $cell_b = str_replace("  ", " ", trim($cell_b), $count);
        } while ($count > 0);
        do {
            $cell_c = str_replace("  ", " ", trim($cell_c), $count);
        } while ($count > 0);
        $cell_a = explode(" ",$cell_a);
        $cell_b = explode(" ",$cell_b);
        $cell_c = explode(" ",$cell_c);

        $cell_a = array( floatval($cell_a[0]), floatval($cell_a[1]), floatval($cell_a[2]) );
        $cell_b = array(  (float)$cell_b[0], floatval($cell_b[1]), floatval($cell_b[2]) );
        $cell_c = array( floatval($cell_c[0]), floatval($cell_c[1]), floatval($cell_c[2]) );
        
        //print_r( $cell_a);

        if($cell_a[0]!=$cell_b[1] && $cell_a[0]!=$cell_c[2] && $cell_b[1]!=$cell_c[2] &&
            $cell_a[1]==0 && $cell_a[2]==0 &&
            $cell_b[0]==0 && $cell_b[2]==0 &&
            $cell_c[0]==0 && $cell_c[1]==0 ){
            echo "Ortorr&ocirc;mbico";

        }else if($cell_a[0]==$cell_b[1] && $cell_b[0]!=$cell_c[2] &&
            $cell_a[1]==0 && $cell_a[2]==0 &&
            $cell_b[0]==0 && $cell_b[2]==0 &&
            $cell_c[0]==0 && $cell_c[1]==0 ){
            echo "Tetragonal";

        }else if($cell_a[0]==$cell_b[1] && $cell_b[0]==$cell_c[2] &&
            $cell_a[1]==$cell_a[2] &&
            $cell_b[0]==$cell_b[2] &&
            $cell_c[0]==$cell_c[1] &&
            $cell_a[1]==$cell_b[0] &&
            $cell_b[0]==$cell_c[0]  ){
            echo "Rombo&eacute;drico";

        }else if($cell_a[0]==$cell_b[1] && $cell_a[0]==$cell_c[2] && $cell_b[1]==$cell_c[2] &&
            $cell_a[1]==0 && $cell_a[2]==0 &&
            $cell_b[0]==0 && $cell_b[2]==0 &&
            $cell_c[0]==0 && $cell_c[1]==0 ){
            echo "C&uacute;bico";

        }else if($cell_a[0]!=$cell_b[1] && $cell_a[0]!=$cell_c[2] && $cell_b[1]!=$cell_c[2] &&
            $cell_a[1]!=$cell_a[2] &&
            $cell_b[0]!=$cell_b[2] &&
            $cell_c[0]!=$cell_c[1] &&
            $cell_a[1]!=$cell_b[0] &&
            $cell_b[0]!=$cell_c[0]  ){

            echo "Tricl&iacute;nico";
        }
    }
}


?>