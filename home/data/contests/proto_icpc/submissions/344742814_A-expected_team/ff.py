b = int(input().strip())
m = int(input().strip())

def derivative(b, m):
    d = b * m ** (b - 1)
    return d


print(derivative(b, m))



