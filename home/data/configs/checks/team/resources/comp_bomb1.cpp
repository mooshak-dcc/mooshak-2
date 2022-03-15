
#include<iostream>
using namespace std;
template <class T>
struct Bomb {
    static int constexpr value() {
        return Bomb<Bomb<T>>::value();
    }
};
int main()
{
    Bomb<int> b;
    cout << b.value() << endl;
    return 0;
}
