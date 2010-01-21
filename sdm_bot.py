#!/usr/local/bin/python2.6

import os
import pysvn
import subprocess
import sys
import time

class MockClient:
  def start(self):
    # returns the last revision handled or None
    return 7420

  def report_result(self, agent, branch, revision, result):
    # This is also used to communicate errors in building and running
    # tests.
    return True

class Agent:
  BASE_URL = "http://google-web-toolkit.googlecode.com/svn/"

  def __init__(self, name, client, working_dir, branch = 'trunk'):
    self.client = client
    self.working_dir = working_dir
    self.branch = branch
    self.name = name

  def _path_for_branch(self):
    return os.path.join(self.working_dir, self.branch)

  def _path_for_tools(self):
    return os.path.join(self.working_dir, 'tools')

  def _url_for_branch(self):
    return Agent.BASE_URL + self.branch

  def _url_for_tools(self):
    return Agent.BASE_URL + 'tools'

  def _updateWorkingCopy(self, svn, url, path, revision):
    revision = pysvn.Revision(pysvn.opt_revision_kind.number, revision)
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
    client = self.client
    self.last_revision = client.start()
    # Send request to dashboard. (start: name, branch)
    # Store operating state locally. {revision: x}

  def run_tests(self, svn, revision):
    print "Running tests on r%d" % revision
    # TODO(knorton):
    # (1) Checkout/Update GWT branch to revision.
    print "Updating %s" % self.branch
    self._updateWorkingCopy(svn, self._url_for_branch(), self._path_for_branch(), revision)
    # (2) Checkout/Update GWT tools to HEAD.
    print "Updating tools"
    self._updateWorkingCopy(svn, self._url_for_tools(), self._path_for_tools(), revision)
    # (3) Build GWT
    # TODO(knorton): Check ant and don't assume it's on the path.
    status, stdout, stderr = self._execute("ant")
    if status != 0:
      print "Build Failed"
    # (4) Run Tests.

  def run(self):
    svn = pysvn.Client()
    last_revision = self.last_revision
    if last_revision:
      log = svn.log(self._url_for_branch(), revision_end = pysvn.Revision(pysvn.opt_revision_kind.number, last_revision + 1))
      log.reverse()
      print "Processing %d revisions." % len(log)
      for change in log:
        revision = change.data['revision'].number
        self.run_tests(svn, revision)
        self.client.report_result(self.name, self.branch, revision, [])
        self.last_revision = revision
    else:
      pass # TODO(knorton): Build the latest revision.

def Main(args):
  # TODO(knorton): Add options parsing.

  agent = Agent(name = 'kellegous_primary',
      client = MockClient(),
      working_dir = 'working',
      branch = 'trunk')

  agent.start()
  while True:
    agent.run()
    time.sleep(10)

if __name__ == '__main__':
  Main(sys.argv[1:])
