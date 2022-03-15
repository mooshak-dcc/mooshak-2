#include<stdio.h>
int main(){
int i,ns,n,cont;
cont=0;
scanf("%d\n",&ns);
if((ns>=3)&&(ns<=1000)){
    for(i=1;i<=ns;i--){
        scanf("%d\n",&n);
        }
        if((n*2>i-1)&&(n*2>i+1)){
            cont++;
        }
    }else{
        printf("ERROR\n");
    }
    printf("%d\n",cont);
}
