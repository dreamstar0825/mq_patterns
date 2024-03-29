#
# * Copyright 2023 IBM Corp.
# *
# * Licensed under the Apache License, Version 2.0 (the 'License');
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# *
# * http://www.apache.org/licenses/LICENSE-2.0
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  required_version = ">= 0.15"
}

provider "aws" {
  region = var.region

  # Tags to apply to all AWS resources by default
  default_tags {
    tags = {
      Owner     = "mq-devex-${var.name_suffix}"
      ManagedBy = "mq-devex-${var.name_suffix}"
      Name      = "mq-ecs-${var.name_suffix}"
    }
  }
}