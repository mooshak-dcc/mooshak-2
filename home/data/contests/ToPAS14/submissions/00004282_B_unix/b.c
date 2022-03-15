#include <stdio.h>

main(){
int vet[1000];
int i;
int q;
int n=0;
scanf("%d",&i);
if(i<3 || i>1000)
{}
else{
for(q=1;q<=i;q++){
scanf("%d",&vet[q]);
}
for(q=1;q<=i;q++){
if(vet[q-1]*2<vet[q]){
if(vet[q+1]*2<vet[q]){
n=n+1;
}
}
}
}
printf("%d\n",n);
return 0;
}
