#!/usr/bin/env python
"""

Provider node that refuses to compute the task
because of a docker image problem

"""

import mock
import sys

from evera_messages import message
from scripts.node_integration_tests import params

from everaapp import start

sys.argv.extend(params.PROVIDER_ARGS_DEBUG)


def wrong_docker_images(self, ctd):
    self.err_msg = message.CannotComputeTask.REASON.WrongDockerImages
    return False


with mock.patch("evera.task.tasksession.TaskSession._set_env_params",
                wrong_docker_images):
    start()
