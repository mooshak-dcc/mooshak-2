#include <stdio.h>
#include <stdlib.h>

int main()
{
    int x, i, conta=0,j;

    scanf("%d", &x);

    int v[x];

    for(i=0;i<x;i++){
    scanf("%d", &v[i]);
    }

    for(j=1;j<x;j++){
        if(v[j]>v[j-1]*2 && v[j]>v[j+1]*2){
            conta++;
        }
    }

    printf("%d\n",conta);
    return 0;
}
