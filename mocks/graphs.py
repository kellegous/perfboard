import math
def decr_data(n = 100, base = 24000):
  data = [base + 1000]
  for i in xrange(n - 1):
    if i % 2 == 0:
      data.append(base)
    else:
      data.append(base - 500)
  return data

def random_data(n = 100, base = 24000):
  data = []
  for i in xrange(n):
    r = random()
    if r > 0.8:
      base = base + random(-1.0, 1.0) * 0.1 * base
    data.append(base)
  return data

def graph_b(data, w = 800, h = 200):
  stroke(0, 68.0 / 256.0, 1.0)
  nofill()
  
  nn, lo, hi = len(data), min(data), max(data)
  
  dx = float(w) / float(nn)
  dy = float(h) / float(hi)

  y0 = h - dy * data[0]
  print "(%s, %s)" % (0, y0)
  beginpath(0, y0)
  for x in xrange(1, len(data)):
    y = dy * data[x]
    print "(%s, %s)" % (dx * x, h - y)
    lineto(dx * x, h - y)
  moveto(0, y0)
  endpath()

def graph(data, w = 800, h = 200):
  nostroke()
  fill(0, 68.0 / 256.0, 1.0)
  nn, lo, hi = len(data), min(data), max(data)
  dx = float(w) / float(nn)
  dy = float(h) / float(hi)
  
  for x in xrange(len(data)):
    y = dy * data[x]
    rect(dx * x, h - y, dx * .6, y)

data = random_data(400)
graph_b(data, 1200, 50)
#translate(0, 100)
#graph(data, 1200, 50)