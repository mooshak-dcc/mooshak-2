#include<stdio.h>
int main()
{
    int ns,n,i,conta=0;
    scanf("%d",&ns);
    if((ns>=3)&&(ns<=1000))
    {
        for(i=0; i<ns; i++)
        {
            scanf("%d",&n);
        }
        if((n*2>i-1)&&(n*2>i+1))
        {
            conta=conta+1;
        }
    }
    else
    {
        printf("ERROR");
    }
    printf("%d",conta);
    return 0;
}
