import unittest

from evera.core.variables import PROTOCOL_CONST

from .base import NodeTestBase


class EveraNodeTest(NodeTestBase, unittest.TestCase):

    def test_regular_task_run(self):
        exit_code = self._run_test('evera.regular_run.RegularRun')
        self.assertEqual(exit_code, 0)

    def test_no_concent(self):
        exit_code = self._run_test('evera.no_concent.NoConcent')
        self.assertEqual(exit_code, 0)

    def test_rpc(self):
        exit_code = self._run_test('evera.rpc_test.RPCTest')
        self.assertEqual(exit_code, 0)

    def test_rpc_mainnet(self):
        exit_code = self._run_test(
            'evera.rpc_test.MainnetRPCTest', '--mainnet')
        self.assertEqual(exit_code, 0)

    def test_task_timeout(self):
        exit_code = self._run_test('evera.task_timeout.TaskTimeoutAndRestart')
        self.assertEqual(exit_code, 0)

    def test_frame_restart(self):
        exit_code = self._run_test('evera.restart_frame.RestartFrame')
        self.assertEqual(exit_code, 0)

    @unittest.skipIf(PROTOCOL_CONST.ID <= '29', "Known issue in 0.18.x")
    def test_exr(self):
        exit_code = self._run_test('evera.exr.RegularRun')
        self.assertEqual(exit_code, 0)

    def test_jpeg(self):
        exit_code = self._run_test('evera.jpeg.RegularRun')
        self.assertEqual(exit_code, 0)

    def test_jpg(self):
        exit_code = self._run_test('evera.jpg.RegularRun')
        self.assertEqual(exit_code, 0)

    def test_nested(self):
        exit_code = self._run_test(
            'evera.regular_run_stop_on_reject.RegularRun',
            **{'task-package': 'nested'}
        )
        self.assertEqual(exit_code, 0)

