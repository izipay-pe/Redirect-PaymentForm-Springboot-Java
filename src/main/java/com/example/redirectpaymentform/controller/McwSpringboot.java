package com.example.redirectpaymentform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Controller
public class McwSpringboot {
    
    String currencyType = null;
    String currency = null;

    @Autowired
    private McwController mcwController;
    
    /**
     * @@ Manejo de solicitudes GET para la ruta raíz @@
     */
    @GetMapping("/")
    public String showIndexPage(Model model) {
	String orderId = mcwController.generateOrderId();
	model.addAttribute("orderId", orderId);
        return "index";
    }
    
    /**
     * @@ Manejo de solicitudes POST para checkout @@
     */
    @PostMapping("/checkout")
    public String processCheckout(
	@RequestParam Map<String, String> parameters,
	Model model
	) {
	
	// Calcular el Signature y los valores dinámicos para el formulario
	Map<String, String> result = mcwController.dataForm(parameters);
	
	// Obtener el valor de la moneda
	currency = parameters.get("currency");
	
	// Asignar el tipo de moneda correspondiente
	if ("604".equals(currency)){
		currencyType = "Soles";
	} else {
		currencyType = "Dólares";
	}
	
	// Agrerar valores para ser usado en el template
	model.addAttribute("parameters", result);
	model.addAttribute("amount", parameters.get("amount"));
	model.addAttribute("currency", currencyType);

    	return "checkout";
    }
    
   /**
     * @@ Manejo de solicitudes POST para result @@
     */
    @PostMapping("/result")
    public String processResult(
        // Procesando datos POST enviados de la respuesta de Izipay
	@RequestParam Map<String, String> resultParameters,
        Model model
    ) {
	
	// Válida que la respuesta sea íntegra comparando el signature recibido con el generado
	if (!mcwController.checkSignature(resultParameters)){
		return "error";
	}

	currency = resultParameters.get("vads_currency");

	if("604".equals(currency)) {
		currencyType = "PEN";
	} else {
		currencyType = "USD";
	}
	
	// Almacena algunos datos de la respuesta en variables
	String orderTotalAmount = resultParameters.get("vads_amount");
	double orderAmountdouble = Double.parseDouble(orderTotalAmount) / 100;
    	String orderAmount = String.format("%.02f", orderAmountdouble);
		
	// Agrega los datos al modelo
	model.addAttribute("amount", orderAmount);
	model.addAttribute("parameters", resultParameters);
	model.addAttribute("currency", currencyType);
		
	return "result";
	
    }
    
    /**
     * @@ Manejo de solicitudes POST para IPN @@
     */
    @PostMapping("/ipn")
    @ResponseBody
	public String processIpn(
		@RequestParam Map<String, String> ipnParameters
    	) {
	
	// Válida que la respuesta sea íntegra comprando el signture recibido con el generado
	if (!mcwController.checkSignature(ipnParameters)){
		return "No valid IPN";
	}

	// Almacena algunos datos de la respuesta IPN en variables
	String orderStatus = ipnParameters.get("vads_trans_status");
	String orderId = ipnParameters.get("vads_order_id");
	String uuid = ipnParameters.get("vads_trans_uuid");
	
	// Enviando en la respuesta el Order Status
	return "OK! Order Status: " + orderStatus;


    }
}
