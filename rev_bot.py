#!/usr/local/bin/python2.6

import datetime
import optparse
import pysvn
import rpc
import sys
import time

def FormatMessage(message):
  def Split(message):
    return message.split("\n")

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
  return Reline(Trim(Split(message)), 80)

def GetSvnRevision(revision):
  if revision.has_key('id'):
    return pysvn.Revision(pysvn.opt_revision_kind.number, revision['id'][1:])
  return None

def Main():
  parser = optparse.OptionParser()
  parser.add_option("--interval", dest="interval", default=30,
                    help="Polling interval in seconds.", type="int")
  parser.add_option("--backend", dest="backend", default="http://localhost:8888/rpc",
                    help="The backend rpc server")
  options, args = parser.parse_args()

  url = "http://google-web-toolkit.googlecode.com/svn/"

  client = rpc.Client(options.backend)
  svn = pysvn.Client()

  revision = GetSvnRevision(client.GetLatestRevision())
  while True:
    if revision:
      log = svn.log(url, revision_end = revision)
      # The first item in the list will include the end revision.
      if len(log) > 0:
        log = log[:-1]
    else:
      log = svn.log(url)
    log.reverse()

    for change in log:
      data = change.data
      client.RecordRevision("r%d" % data['revision'].number,
          data['author'],
          FormatMessage(data['message']),
          datetime.datetime.fromtimestamp(data['date']))
      revision = data['revision']
    time.sleep(options.interval)

if __name__ == '__main__':
  Main()
