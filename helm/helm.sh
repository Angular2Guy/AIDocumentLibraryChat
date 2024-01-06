#!/bin/sh
helm delete aidocumentlibrarychat
helm install aidocumentlibrarychat ./  --set serviceType=NodePort