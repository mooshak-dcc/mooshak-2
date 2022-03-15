#include <stdio.h>
#include <stdlib.h>

int main()
{
    int freqs[1000];
    int temp, numero, i;
    int cont = 0;

    scanf("%d", &numero);
    for (i = 0; i < numero; i++){
        scanf("%d", &temp);
        freqs[i] = temp;
        if (i >= 2){
            if (freqs[i - 1] > freqs[i - 2] * 2 && freqs[i - 1] > freqs[i] * 2){
                cont += 1;
            }
        }
    }


    printf("%d\n", cont);
    return 0;
}
