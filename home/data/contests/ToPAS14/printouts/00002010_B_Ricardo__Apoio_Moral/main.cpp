#include <iostream>

using namespace std;

int main()
{
   int N;

   cin>>N;
   int ant;
   int ant1;
   int atual;
   int cont=0;

   cin>>ant1;
   cin>>ant;
   cin>>atual;
   for(int i=0; i<N-2; i++){
    if (((ant1*2)<ant)&&(ant>(atual*2))){
    cont++;
    }
   ant1=ant;
   ant=atual;
   if(i<N-3){
   cin>>atual;
   }
   }
   cout<<cont<<endl;
    return 0;
}
