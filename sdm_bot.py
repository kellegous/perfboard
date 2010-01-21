#!/usr/local/bin/python2.6

import os
import pysvn
import sys
import time

class MockDashboard:
  def start(self):
    # returns the last revision handled or None
    pass

  def report_result(self, agent, branch, result):
    # returns success or failure.
    pass

class Agent:
  BASE_URL = "http://google-web-toolkit.googlecode.com/svn/"

  def __init__(self, name, dashboard_url, working_dir, branch = 'trunk'):
    self.dashboard_url = dashboard_url
    self.working_dir = working_dir
    self.branch = branch
    self.name = name

  def start(self):
    pass
    # Send request to dashboard. (start: name, branch)
    # Store operating state locally. {revision: x}

  def run(self):
    print "run"
    # Request an svn log starting from the last revision.
    # For each revision:
    #   perform tests
    #   send results to dashboard

def Main(args):
  # TODO(knorton): Add options parsing.

  agent = Agent(name = 'kellegous_primary',
      dashboard_url = 'http://localhost:5554/m',
      working_dir = 'working',
      branch = 'trunk')

  agent.start()
  while True:
    agent.run()
    time.sleep(10)

if __name__ == '__main__':
  Main(sys.argv[1:])
