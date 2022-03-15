#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main()
{
    int i, j, N, x, y, tmpx, tmpy;
    int c[200][2], nC, distancias[200], distancia, pos;
    char tipo[10], tmptipo[10];

    scanf("%d %d %s", &x, &y, tipo);
    scanf("%d", &N);

    nC = 0;
    for(i=0; i<N; i++) {
        scanf("%d %d %s", &tmpx, &tmpy, tmptipo);

        if(strcmp(tipo, tmptipo) == 0) {
            c[nC][0] = tmpx;
            c[nC][1] = tmpy;
            nC++;
        }
    }

    distancia = 10000000;
    for(i=0; i<nC; i++) {
        distancias[i] = (x-c[i][0])*(x-c[i][0]) + (y-c[i][1])*(y-c[i][1]);
        if(distancias[i] < distancia) {
            pos = i;
            distancia = distancias[i];
        }
    }

    if(nC == 0) printf("Nenhum\n");
    else printf("%d %d %s %d\n", c[pos][0], c[pos][1], tipo, distancia);

    return 0;
}
