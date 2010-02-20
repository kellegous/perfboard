#!/usr/local/bin/python2.6

import datetime
import os
import random
import re
import rfc822
import simplejson
import subprocess
import sys

class RandomSeries:
  def __init__(self, base = 0):
    self.__next = base

  def next(self):
    def rand():
      return random.random() * 2.0 - 1.0
    next = self.__next
    r = random.random()
    if r > 0.8:
      next = next + rand() * 0.1 * next
    self.__next = next
    return (int(next), int(next + next * 0.1))

def LoadLog(path):
  def ExtractField(line, prefix):
    assert line.startswith(prefix)
    return line[len(prefix):]

  def ParseAuthor(raw):
    ix = raw.find(' <')
    if ix <= 0:
      return ix
    else:
      return raw[:ix]

  def ParseDate(raw):
    return rfc822.parsedate(raw)[:6]

  def ParseMessage(message):
    p = re.compile("^git-svn-id: https://google-web-toolkit.googlecode.com/svn/trunk@(\\d+)")
    while True:
      line = message.pop()
      m = p.search(line)
      if m:
        return (message, int(m.groups()[0]))
    assert False

  def FormatMessage(message):
    def Reline(message, size):
      data = []
      for line in message:
        if len(line) <= size:
          data.append(line)
        else:
          # split on word boundary
          words = line.split(" ")
          line_of_words = []
          len_of_line = 0
          while len(words) > 0:
            word = words[0]
            if len(word) + 1 + len_of_line > size:
              data.append(" ".join(line_of_words))
              line_of_words = []
              len_of_line = 0
              if len(word) >= size:
                data.append(word)
                words = words[1:]
            else:
              line_of_words.append(word)
              len_of_line += len(word) + 1
              words = words[1:]
          if len(line_of_words) > 0:
            data.append(" ".join(line_of_words))
      return data
          
    def Trim(message):
      start = 0
      end = len(message) - 1
      while len(message[start]) == 0 and start < end:
        start += 1
      if start == end:
        return [""]
      while len(message[end]) == 0 and start < end:
        end -= 1
      return message[start:end + 1]
    return Reline(Trim(message), 80)

  proc = subprocess.Popen(['git', 'log'], stdout=subprocess.PIPE, cwd = path)
  (stdout, stderr) = proc.communicate()
  lines = stdout.split("\n")
  x = 0
  while True:
    if x == len(lines):
      break

    # commit
    commit = ExtractField(lines[x], 'commit ')
    x += 1

    # author
    author = ParseAuthor(ExtractField(lines[x], 'Author: '))
    x += 1

    # date
    date = ParseDate(ExtractField(lines[x], 'Date:   '))
    x += 1

    # message
    message = []
    while x < len(lines) and not lines[x].startswith('commit '):
      message.append(lines[x][4:])
      x += 1

    message, svn_id = ParseMessage(message)
    message = FormatMessage(message)

    yield { 'revision' : svn_id, 'author' : author, 'date' : date, 'message' : message }

def TransformLogs(logs, series):
  result = []
  for log in logs:
    data = {}
    for name, item in series.items():
      data[name] = item.next()
    log['data'] = data
    result.append(log)
  return result

def Main(args):
  if len(args) != 1:
    sys.stderr.write("bad args\n")
    sys.exit(1)

  if not os.path.exists(args[0]):
    sys.stderr.write("%s is not a directory\n" % args[0])
    sys.exit(1)

  if not os.path.exists(os.path.join(args[0], '.git')):
    sys.stderr.write("%s is not a .git repository\n" % args[0])
    sys.exit(1)

  series = {
    'showcase' : RandomSeries(300000),
    'mail' : RandomSeries(150000),
    'json' : RandomSeries(100000),
    'hello' : RandomSeries(60000),
    'dynatable' : RandomSeries(80000),
  }
  print simplejson.dumps(TransformLogs(LoadLog(args[0]), series), indent=4)

if __name__ == '__main__':
  Main(sys.argv[1:])
