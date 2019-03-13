#!/usr/bin/env python
"""

Requestor Node not sending out the payments

"""
import mock
import sys

from scripts.node_integration_tests import params

from everaapp import start  # noqa: E402 module level import not at top of file

sys.argv.extend(params.REQUESTOR_ARGS_DEBUG)


with mock.patch(
        'evera.ethereum.paymentprocessor.PaymentProcessor.sendout',
        mock.Mock(return_value=True),
):
    start()
