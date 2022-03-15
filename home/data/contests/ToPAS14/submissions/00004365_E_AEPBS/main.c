#include <stdio.h>
#include <stdlib.h>

char dicionario[1000][21];

int main()
{
    int numero = 0;
    int i = 0;
    scanf("%d", &numero);
    for (i = 0; i < numero; i++){
        scanf("\n%s", dicionario[i]);
    }

    int action = 1;
    int i2 = 0;
    int palavras;
    char palavra[20];
    while(action != 0){
        scanf("%d", &action);
        if (action== 0) continue;

        scanf("%d ", &palavras);
        for(i = 0; i < palavras; i+= 1){
            if (action == 1){
                scanf("%s ", palavra);
                for(i2 = 0; i2 < numero; i2 += 1){
                    if (strcmp(dicionario[i2], palavra) == 0){
                        printf("%d", i2 + 1);
                        break;
                    }
                }
            } else if (action == 2){
                scanf("%d", &i2);
                printf("%s", dicionario[i2 - 1]);
            }
            if (i < palavras - 1) printf(" ");
        }
        printf("\n");
    }
    return 0;
}
