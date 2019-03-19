package blockchain

import "github.com/ethereum/go-ethereum/common"


// StandardToken topics
var (
	TransferTopic = common.HexToHash("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef")
	ApprovalTopic = common.HexToHash("0x8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925")
)

// Ownable topics
var (
	OwnershipTransferredTopic = common.HexToHash("0x8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0")
)

// SNMT topics
var (
	GiveAwayTopic = common.HexToHash("0xe08e9d066634006283658128ec91f58d444719d7a07d49f72924da4352ff94ad")
)

// MultiSig topics
var (
	ConfirmationTopic      = common.HexToHash("0x4a504a94899432a9846e1aa406dceb1bcfd538bb839071d49d1e5e23f5be30ef")
	RevocationTopic        = common.HexToHash("0xf6a317157440607f36269043eb55f1287a5a19ba2216afeab88cd46cbcfb88e9")
	SubmissionTopic        = common.HexToHash("0xc0ba8fe4b176c1714197d43b9cc6bcf797a4a7461c5fe8d0ef6e184ae7601e51")
	ExecutionTopic         = common.HexToHash("0x33e13ecb54c3076d8e8bb8c2881800a4d972b792045ffae98fdf46df365fed75")
	ExecutionFailureTopic  = common.HexToHash("0x526441bb6c1aba3c9a4a6ca1d6545da9c2333c8c48343ef398eb858d72b79236")
	DepositTopic           = common.HexToHash("0xe1fffcc4923d04b559f4d29a8bfc6cda04eb5b0d3c460751c2402c5c5cc9109c")
	OwnerAdditionTopic     = common.HexToHash("0xf39e6e1eb0edcf53c221607b54b00cd28f3196fed0a24994dc308b8f611b682d")
	OwnerRemovalTopic      = common.HexToHash("0x8001553a916ef2f495d26a907cc54d96ed840d7bda71e73194bf5a9df7a76b90")
	RequirementChangeTopic = common.HexToHash("0xa3f1ee9126a074d9326c682f561767f710e927faa811f7a99829d49dc421797a")
)
