package com.pg;

import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

public class KubeJavaClientTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    String kubeConfigPath = "C:\\Users\\pg939j\\.kube\\config";

	    ApiClient client;
		try {
			ApiCallback<V1Pod> apiCallback = new ApiCallbackResponse();
			//client = Config.defaultClient();
			 client =ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
			Configuration.setDefaultApiClient(client);

		    CoreV1Api api = new CoreV1Api();
		    V1PodList list =
		        api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		    V1Pod oldPod = null;
		    for (V1Pod item : list.getItems()) {
		    	if(item.getMetadata().getName().contains("old-pod")) {
		    		oldPod = item;
		    	}
		        //System.out.println(item.getMetadata().getName());
		      }
			
		    V1Pod newPod = createPod(oldPod);
		    
		    System.out.println("Before creating Pod : "+newPod.getMetadata().getName());
		    api.createNamespacedPodAsync("com-att-oce", newPod, "true", "false", "", apiCallback);
		    System.out.println("After creating Pod : "+newPod.getMetadata().getName());

		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

	}
	
	public static V1Pod createPod(V1Pod oldPod) {
		V1ObjectMeta meta = new V1ObjectMeta();
		meta.name("my-new-pod");

		V1EnvVar addr = new V1EnvVar();
		addr.name("var1");
		addr.value("value1");

		V1EnvVar port = new V1EnvVar();
		port.name("var2");
		port.value("value2");

		/*V1ResourceRequirements res = new V1ResourceRequirements();
		Map<String, String> limits = new HashMap<>();
		limits.put("cpu", "300m");
		limits.put("memory", "500Mi");
		res.limits(limits);*/

		V1Container container = new V1Container();
		container.name("my-name");
		container.image("my-image");
		container.env(Arrays.asList(addr, port));
		container.resources(oldPod.getSpec().getContainers().get(0).getResources());

		V1PodSpec spec = new V1PodSpec();
		spec.containers(Arrays.asList(container));

		V1Pod podBody = new V1Pod();
		podBody.apiVersion("v1");
		podBody.kind("Pod");
		podBody.metadata(meta);
		podBody.spec(spec);
		
		return podBody;
	}
	
}