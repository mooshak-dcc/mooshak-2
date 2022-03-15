#include<iostream>
#include<typeinfo>
#include<utility>
using namespace std;

template <int D, typename A,typename B>
struct foo {
  typedef typename foo<D-1, std::pair<A,B>, std::pair<A,B> >::type type;
};
template <typename A,typename B>
struct foo<0,A,B> {
  typedef std::pair<A,B> type;
};
int main(){
  foo<2,int,int>::type var;
}
