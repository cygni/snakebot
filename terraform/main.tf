terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "5.19.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.27.0"
    }
  }
}

provider "google" {
  credentials = file("account.json")

  project = local.project
  region  = local.region
  zone    = local.zone
}

data "google_client_config" "default" {
  depends_on = [module.gke_cluster]
}

data "google_container_cluster" "default" {
  name       = local.cluster_name
  depends_on = [module.gke_cluster]
}

provider "kubernetes" {
  host  = "https://${data.google_container_cluster.default.endpoint}"
  token = data.google_client_config.default.access_token
  cluster_ca_certificate = base64decode(
    data.google_container_cluster.default.master_auth[0].cluster_ca_certificate,
  )
}

module "gke_cluster" {
  source       = "./gke_cluster"
  cluster_name = local.cluster_name
  region       = local.region
}

module "kubernetes_config" {
  source = "./kubernetes-config"
}

output "cluster_name" {
  value = local.cluster_name
}

output "cluster_endpoint" {
  value = data.google_container_cluster.default.endpoint
}

# output "web_service_ip" {
#   value = kubernetes_service.snakebot_web_service.status.0.load_balancer.0.ingress.0.ip
# }
#
# output "server_service_ip" {
#   value = kubernetes_service.snakebot_server_service.status.0.load_balancer.0.ingress.0.ip
# }
