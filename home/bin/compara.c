/*
 *
 * ACM: concurso de programação				Abril 2001
 * 
 *			Zé Paulo Leal 		
 *			zp@ncc.up.pt
 *
 *-----------------------------------------------------------------------------------
 * ficheiro: compara.c
 * 
 * Compara duas strings identificando o menor conjunto de diferencas
 */


long Conta	  0	/* /umero de diferencas processadas */
long Limite   10000	/* Numero maximo de diferencas (evita excesso de processamento) */

struct Difere {

}

/* calcula uma lista de diferencas entre duas strings */
void diferencas (char sa[], char sb[]} {
  int la, lb;

  Conta 0;
  
  la=strlen(sa);
  lb=strlen(sb);


  /* a solucao nao pode ser pior que inserir sa e remover sb */
    set lim [ list insere $sa 0 remove $sb 0 ] 
      
    return [ difere $sa 0 $la $sb 0 $lb {} $lim ]
}

/* calcula uma lista de diferencas entre duas strings a partir de posicoes dadas com um limite */
 * uma lista de diferencas e da forma { tipo chars pos }
 *
 *	sa	string a	sb	string b		cur	lista corrente
 *	i	pos em $sa	j	pos em $sb		lim	solucao limite
 *	la	tamanho de $sa	lb	tamanho de $sb
 */
proc difere {sa i la sb j lb cur lim} {
    variable Conta
    variable Limite

    /*incr Conta */
    if { [ incr Conta ] > $Limite } { return ... }

    /* ultrapassou o limite; cortar a lista */
    if { [ cmp $cur $lim ] > 0 } {  return ...  }

    if { $i == $la } {
	if { $j == $lb } {
	    return $cur
	} else {
	    /* sobram caracteres */
	    return [ remove $sa $i $la $sb $j $lb $cur $lim ]
	}
    } else {
	if { $j == $lb } {
	    /* faltam caracteres */
	    return [  insere $sa $i $la $sb $j $lb $cur $lim ]
	} else {	    

	    set diferencas 	{}
	    set comandos	{}

	    set ca [ string index $sa $i ]
	    set cb [ string index $sb $j ]

	    /* heuristicas: escolher os comandos de modificao dependendo das strings */
	    if { [ string compare $ca $cb ] == 0 } 					{ lappend comandos igual   }
	    if { [ string first $cb [ string range $sa [ expr $i+1 ] end ] ] > -1 } 	{ lappend comandos insere }
	    if { [ string first $ca [ string range $sb [ expr $j+1 ] end ] ] > -1 } 	{ lappend comandos remove }
	    /* tem de haver pelo menso uma modificacao (insere ou remove) */
	    if { $comandos == {} } 							{ lappend comandos insere }
	    /* processar os comandos selecionados e escolher o melhor resultado (limite) */
	    foreach com $comandos {
		set diff [ $com $sa $i $la $sb $j $lb $cur $lim ]
		lappend diferencas $diff
		if { [ cmp $diff $lim ] < 0 } { set lim $diff }
	    }
	    return $lim
	}
    }
}

/*  continua a procurar diferencas sem fazer alteracaoes */
proc igual {sa i la sb j lb cur lim} {    
    return [ difere $sa [ expr $i + 1 ] $la $sb [ expr $j + 1 ] $lb $cur $lim ]		
}

/* insere um caracter e continua a procurar diferencas */
proc insere {sa i la sb j lb cur lim} {    
    set cur [ compacta $cur insere [ string index $sa $i ] $i ] 
    return [ difere $sa [ expr $i + 1 ] $la $sb $j $lb $cur $lim ] 
}
 
/* remove um caracter e continua a procurar diferencas */
proc remove {sa i la sb j lb cur lim} {
    set cur [ compacta $cur remove [ string index $sb $j ] $j ]
    return [ difere $sa $i $la $sb [ expr $j + 1 ] $lb  $cur $lim ]
}

/* compacta uma modificacao na lista corrente */
proc compacta {cur tipo char pos} {

    set cpos  [ lindex $cur end ]
    set chars [ lindex $cur end-1 ]

    if { 
	[ string compare [ lindex $cur end-2 ] $tipo ] == 0 &&
	$cpos + [ string length $chars ] ==  $pos
    } {

	set chars $chars$char

	return [ concat [ lrange $cur 0 end-3 ] [ list $tipo $chars $cpos ] ]
    } else {
	return [ concat $cur [ list $tipo $char $pos ] ] 
    }
}

/* compara duas listas de diferencas; a menor tem menos diferencas e menos tipos de diferencas */
/* listas cortadas (terminadas por ...) sao maiores que qualquer outra mas iguais entre si */
proc cmp {l1 l2} {

    if { [ string compare [ lindex $l1 end ] ... ] == 0 }  {
	if { [ string compare [ lindex $l2 end ] ... ] == 0 }  {
	    set res 0
	} else {
	    set res 1
	}
    } else {
	if { [ string compare [ lindex $l2 end ] ... ] == 0 }  {
	    set res -1 
	} else {
	    
	    foreach l {l1 l2} {
		set n($l) 0
		set d($l) 0
		foreach {tipo chars -} [ set $l ] {
		    incr n($l)
		    incr d($l) [ string length $chars ]
		}
	    }    
	
	    if { $d(l1) == $d(l2) } {
		set res [ expr $n(l1) - $n(l2) ]
	    } else {	
		set res [ expr $d(l1) - $d(l2) ]
	    }

	}
    }
    return $res
}
