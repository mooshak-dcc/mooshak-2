template<int I>
struct Infinite
{
  enum { value = (I & 0x1)? Infinite<I+1>::value : Infinite<I-1>::value };
};

int main ()
{
  int i = Infinite<1>::value;
}
