#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main()
{
    int xplayer,yplayer,n,menor_x,menor_y,menor_loja=-1,menor_dist=9999999,dist=0,i,a,b;
    char loja_p[2],loja_menor[2];
    scanf("%d %d %s",&xplayer,&yplayer,loja_p);
    scanf("%d",&n);
    int x[n],y[n];
    char loja[n][2];
    for (i=0;i<n;i+=1)
    {
        scanf("%d %d %s",&x[i],&y[i],loja[i]);
        a = xplayer-x[i];
        b = yplayer-y[i];
        dist = (a*a)+(b*b);
        if (strcmp(loja[i],loja_p)==0)
        {
            if (dist<menor_dist)
            {
                menor_dist = dist;
                menor_loja = i;
            }
        }
    }
    if (menor_loja!=-1)
    {
        printf("%d %d %s %d\n",x[menor_loja],y[menor_loja],loja_p,menor_dist);
    }
    else
    {
        printf("Nenhum\n");
    }
    return 0;
}
