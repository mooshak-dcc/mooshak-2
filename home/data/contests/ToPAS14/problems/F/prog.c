// apt


#include <stdio.h>

char Tab[20][20];
int S, M, N, Pos[2]={1,1}, Desnorteado = 0;
int Fora = 0, Aniquilado = 0;
int Desloca[4][2] = {{1,0},{-1,0},{0,1},{0,-1}};


int mov(char direcao) {
  int imov;
  // N - 0, S - 1, E - 2, W - 3
  switch (direcao) {
  case 'N': if (Desnorteado  == 1)
      imov = 1;
    else imov = 0;
    break;
  case 'S': if (Desnorteado  == 1)
      imov = 0;
    else imov = 1;
    break;
  case 'E': imov = 2;
    break;
  default: imov = 3;
  }
  return imov;
}


void executa_movimento(int imov,int d) {
  char c;
  while(d-- > 0 &&  !Aniquilado) {
    if (Pos[0] + Desloca[imov][0] > 0 &&  Pos[1] + Desloca[imov][1] > 0) {
      Pos[0] += Desloca[imov][0];
      Pos[1] += Desloca[imov][1];
      c = Tab[Pos[0]][Pos[1]];
      switch (c) {
      case('V'): 
	Fora = 0;
	break;
      case('I'):
	Fora = 0; 
	S = S + (S*5)/100;
	break;
      case('T'):
	Fora = 0; 
	S = S - (S*10)/100;
	break;
      case('X'):
	Fora = 0;
	if (imov == 0) imov = 1;
	else if (imov == 1) imov = 0;
	if (Desnorteado) Desnorteado = 0;
	else Desnorteado = 1;
	break;
      case('F'): 
	Aniquilado = 1;
	break;
      case('A'): 
	Fora = 1;
      default:
	S += 10;   // visita pos exterior
	break;
      }
    } else {
      d = 0;
      if (Desnorteado) Desnorteado = 0;
    }
  }
}

int main() {
  int i, j, nmovs, imov, d;
  char aux[20];
  

  scanf("%d%d%d",&S,&M,&N);

  for(i=0;i<M;i++) {
    scanf("%s",aux);
    for(j=0;j<N;j++) 
      Tab[M-1-i][j] = aux[j];
  }

  
  scanf("%d",&nmovs);
  while (nmovs-- > 0 && !Aniquilado) {
    scanf("%s",aux);
   
    d = aux[1]-'0';
    imov = mov(aux[0]);
    executa_movimento(imov,d);
  }

  if(Aniquilado) printf("Aniquilado\n");
  else {
    if (Fora) printf("Fora");
    else printf("Dentro");
    printf("(%d,%d):%d:%s\n",Pos[1],Pos[0],S,(Desnorteado? "D":"ND"));
  }  
  return 0;
}
