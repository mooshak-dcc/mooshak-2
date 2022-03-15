#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

/*int lojas[200][200][2];
int conta[200];*/

int main()
{
    int i = 0;
    int numero;
    int posX, posY;
    char dest[4];


    char tempNome[4];
    int tempX, tempY;
    int tempID;

    int minimumX = 0, minimumY = 0, square = -1;
    int first = 1;

    scanf("%d %d %s\n", &posX, &posY, dest);
    scanf("%d", &numero);
    for (i = 0; i < numero; i += 1){
        scanf("%d %d %s\n", &tempX, &tempY, tempNome);
        if (strcmp(tempNome, dest) == 0){
            int dist = abs(posX - tempX) * abs(posX - tempX) + abs(posY - tempY) * abs(posY - tempY);
            if (first == 1 || dist < square){
                square = dist;
                minimumX = tempX;
                minimumY = tempY;
                first = 0;
            }
        }
        /*lojas[tempID][conta[tempID]][0] = posX;
        lojas[tempID][conta[tempID]][1] = posY;*/


    }

    if (first == 0)
        printf("%d %d %s %d\n", minimumX, minimumY, dest, square);
    else
        printf("Nenhum\n");

    return 0;
}
