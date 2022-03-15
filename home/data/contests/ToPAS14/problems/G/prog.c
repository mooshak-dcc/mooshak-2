
// by APT

#include <stdio.h>
#include <stdlib.h>

#define MAXVERTS 200

int Matriz[MAXVERTS][MAXVERTS];


void ler_matriz(){
  int i, j, n;
  scanf("%d",&n);
  for(i=0;i<n;i++) 
    for(j=0;j<n;j++) 
      scanf("%d",&Matriz[i][j]);
}

#define MIN(X,Y) ((X) < (Y)? (X): (Y))

int analisa_rota(int orig, int dest, int ngrupo, int hi, int hf, int k,int h) {
  int nprobs = 0, u, v, ng, po=0, pd=0, d, ho, npessoas = ngrupo;

  // intervalo temporal [hi,hf] em minutos relativo a 7..22 -> 0..15*60
  // po: encontrou origem    pd: encontrou destino 
  // ho: hora de chegada a origem  (h tera a destino)
  scanf("%d",&u);
  if (u == orig) {po = 1; ho = h;}
  while(--k > 0 && pd == 0) {
    scanf("%d%d%d",&ng,&d,&v);
    nprobs += Matriz[u-1][v-1];
    h += d;
    if (po == 1) {
      npessoas = MIN(npessoas,ng);
      if (v == dest) { pd = 1; }
    } else if (v == orig) { po = 1; ho = h;}
    u = v;
  }
  while (k-- > 0)  
    scanf("%d%d%d",&v,&v,&v);  // resto da rota (lixo)

  if (po == 1 && pd == 1 && npessoas == ngrupo && h <= hf && ho >= hi) 
    return nprobs;

  return -1;  // impossivel
}

int main() {
  int origem, destino, ngrupo, nprobs, c, p, hi, hf, k, h;

  scanf("%d%d%d%d%d",&ngrupo,&origem,&destino,&hi,&hf);
  hi = (hi-7)*60;
  hf = (hf-7)*60;
  ler_matriz();
  scanf("%d%d",&k,&h);
  c = 0;
  while (k != 0) {
    nprobs = analisa_rota(origem,destino,ngrupo,hi,hf,k,(h-7)*60);
    if (nprobs != -1) {
      if (c == 0 || nprobs < p) {
	c = 1; p = nprobs;
      } else if (nprobs == p) c++;
    }
    scanf("%d%d",&k,&h);
  }

  if (c == 0) printf("Impossivel\n");
  else 
    printf("%d %d\n",c,p);

  return 0;
}
