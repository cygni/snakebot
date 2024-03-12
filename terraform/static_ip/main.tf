terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "5.19.0"
    }
  }
}

resource "google_compute_address" "static_ip" {
  name   = "static-ip"
  region = var.region

  # lifecycle {
  #   prevent_destroy = true
  # }
}
