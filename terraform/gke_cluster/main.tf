terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "5.19.0"
    }
  }
}

resource "google_container_cluster" "default" {
  name     = var.cluster_name
  location = var.region

  deletion_protection = false

  node_pool {
    name               = "default-pool"
    initial_node_count = 1
  }
}
