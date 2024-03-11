variable "region" {
  description = "The region to deploy to"
  type        = string
  default     = "europe-north1"
}

variable "cluster_name" {
  description = "The name of the GKE cluster"
  type        = string
  default     = "snakebot-cluster"
}
