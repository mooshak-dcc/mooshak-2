#include <stdio.h>
#include <stdlib.h>

int anos[3000][12];
int dados[24];

int main()
{
    int anoAtual, mesAtual;
    int input, numero = 24;
    int i, valor;

    for (i = 0; i < numero; i++){
        scanf("%d %d", &input, &valor);

        dados[i] = valor;
    }

    int mediaTres = (dados[23] + dados[22] + dados[21]) / 3;


    int mediaAnos = (dados[12] + dados[0]) / 2;

    printf("%d\n", (mediaAnos + mediaTres) / 2);
    return 0;
}
