#!/usr/local/bin/python2.6

import glob
import httplib
import os
import pickle
import pysvn
import simplejson
import subprocess
import sys
import time

class Client:
  def __init__(self, host, port = 80):
    self._host = host
    self._port = port

  def _dispatch(self, json):
    http = httplib.HTTPConnection(self._host, self._port)
    http.request('POST', '/perfboard/rpc', simplejson.dumps(json))
    response = http.getresponse()
    return simplejson.loads(response.read())

  def report_result(self, agent, branch, revision, result):
    data = self._dispatch({
      'command' : 'report',
      'agent' : agent,
      'branch' : branch,
      'revision' : revision,
      'data' : result})

  def start(self, agent, branch):
    return 7431
    data = self._dispatch({
      'command' : 'start',
      'agent' : agent,
      'branch' : branch })
    if data.has_key('revision'):
      return data['revision']

class Agent:
  BASE_URL = "http://google-web-toolkit.googlecode.com/svn/"

  def __init__(self, name, client, working_dir, branch = 'trunk'):
    self.client = client
    self.working_dir = working_dir
    self.branch = branch
    self.name = name

    self._report_failures = []

  def _path_for_branch(self):
    return os.path.join(self.working_dir, self.branch)

  def _path_for_tools(self):
    return os.path.join(self.working_dir, 'tools')

  def _url_for_branch(self):
    return Agent.BASE_URL + self.branch

  def _url_for_tools(self):
    return Agent.BASE_URL + 'tools'

  def _updateWorkingCopy(self, svn, url, path, revision = None):
    if revision:
      revision = pysvn.Revision(pysvn.opt_revision_kind.number, revision)
    else:
      revision = pysvn.Revision(pysvn.opt_revision_kind.head)

    if os.path.exists(path):
      svn.update(path, revision = revision)
    else:
      svn.checkout(url, path, revision = revision)

  def _execute(self, command, cwd = None):
    if not cwd:
      cwd = self._path_for_branch()
    proc = subprocess.Popen(command, cwd = cwd, stdout = subprocess.PIPE, stderr = subprocess.PIPE)
    stdout, stderr = proc.communicate()
    return (proc.returncode, stdout, stderr)

  def start(self):
    # Always cleanup the working copies on start.
    svn = pysvn.Client()
    svn.cleanup(self._path_for_branch())
    svn.cleanup(self._path_for_tools())
    self.last_revision = self.client.start(self.name, self.branch)

  def _run_sample_size_test(self, path):
    def avg(data):
      s = 0
      n = 0
      for i in data:
        s += i
        n += 1
      return float(s) / float(n)

    def sample_stats(name):
      sample_path = os.path.join(path, 'build/staging/gwt-0.0.0/samples/%s/war/%s' % (name, name.lower()))
      sizes = [os.path.getsize(file) for file in glob.glob(sample_path + '/*.cache.html')]
      return (min(sizes), avg(sizes), max(sizes))

    return {
      'showcase'  : sample_stats('Showcase'),
      'mail'      : sample_stats('Mail'),
      'json'      : sample_stats('JSON'),
      'hello'     : sample_stats('Hello'),
      'dynatable' : sample_stats('DynaTable'),
    }

  def run_tests(self, svn, revision):
    print "Running tests on r%d" % revision
    # TODO(knorton):
    # (1) Checkout/Update GWT branch to revision.
    print "Updating %s to r%d" % (self.branch, revision)
    self._updateWorkingCopy(svn, self._url_for_branch(), self._path_for_branch(), revision)
    # (2) Checkout/Update GWT tools to HEAD.
    print "Updating tools"
    self._updateWorkingCopy(svn, self._url_for_tools(), self._path_for_tools())
    # (3) Build GWT
    # TODO(knorton): Check ant and don't assume it's on the path.
    print "Building GWT"
    status, stdout, stderr = self._execute("ant")
    if status != 0:
      # TODO(knorton): Log a build failure.
      print "Build Failed"
    # (4) Run Tests.
    # (4.1) Collect size information of samples.
    # (4.2) Run maark benchmarks and collect size and timing info.
    return {
      'sample_size' : self._run_sample_size_test(self._path_for_branch()),
    }

  def _resend_previous_failures(self, client):
    try:
      while len(self._report_failures) > 0:
        failure = self._report_failures[0]
        client.report_result(self.name, self.branch, failure[0], failure[1])
        self._report_failures = failures[1:]
      return True
    except:
      # If there are any failures, stop sending.
      return False

  def run(self):
    # If there were reporting errors on a past run, try to resend.
    self._resend_previous_failures(self.client)
    # TODO(knorton): I may need to limit the # of pending failures.

    svn = pysvn.Client()
    last_revision = self.last_revision
    if last_revision:
      log = svn.log(self._url_for_branch(), revision_end = pysvn.Revision(pysvn.opt_revision_kind.number, last_revision))
      # The last one in the list will always be last_revision
      if len(log) > 0:
        log = log[:-1]
      log.reverse()
    else:
      log = svn.log(self._url_for_branch(), limit = 1)

    print "Processing %d revisions." % len(log)
    for change in log:
      revision = change.data['revision'].number
      results = self.run_tests(svn, revision)
      try:
        self.client.report_result(self.name, self.branch, revision, results)
        self.last_revision = revision
      except:
        self._report_failures.append((revision, results))

def Main(args):
  # TODO(knorton): Add options parsing.

  agent = Agent(name = 'kellegous_primary',
      client = Client('localhost', 8888),
      working_dir = 'working',
      branch = 'trunk')

  agent.start()
  while True:
    try:
      agent.run()
    except Exception as e:
      print str(e)
    time.sleep(30)

if __name__ == '__main__':
  Main(sys.argv[1:])
