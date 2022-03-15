#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char dic[1000][21], aCodificar[21];

void printInt(int N) {
    int i;

    for(i=0; i<N; i++) {
        if(strcmp(dic[i], aCodificar) == 0) {
            printf("%d", i+1);
            break;
        }
    }

    return;
}

int main()
{
    int N, op, c, i, aLer;

    scanf("%d", &N);
    for(i=0; i<N; i++) {
        scanf("%s", dic[i]);
    }

    scanf("%d", &op);
    while(op != 0) {
        if(op == 1) {
            scanf("%d", &c);
            for(i=0; i<c; i++) {
                scanf("%s", aCodificar);
                printInt(N);
                if(i+1 != c) printf(" ");
            }
            printf("\n");
        } else {
            scanf("%d", &c);
            for(i=0; i<c; i++) {
                scanf("%d", &aLer);
                printf("%s", dic[aLer-1]);

                if(i+1 != c) printf(" ");
            }
            printf("\n");
        }

        scanf("%d", &op);
    }

    return 0;
}
