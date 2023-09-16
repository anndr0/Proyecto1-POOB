import java.util.HashMap;
import java.util.Arrays;
/** NPTensor.java
 * 
 * @Ana María Durán y Laura Natalia Rojas
 */
    
public class NPTensor {

    private HashMap<String, Tensor> variables;
    private boolean operationSuccess;
    
    public NPTensor() {
        variables = new HashMap<>();
        operationSuccess = true;
    }

    // Assign a tensor to a variable with all elements having the same value
    public void assign(String name, int[] shape, int value) {
        int[] values = new int[calculateSize(shape)];
        Arrays.fill(values, value);
        Tensor tensor = new Tensor(shape, values);
        variables.put(name,tensor);

    }

    // Assign a tensor to a variable with the given values
    public void assign(String name, int[] shape, int[] values) {
        if (shape.length == 0 || calculateSize(shape) != values.length) {
            operationSuccess = false;
            return;
        }
        Tensor tensor = new Tensor(shape, values);
        variables.put(name, tensor);
        operationSuccess = true;
    }

    // Assigns the value of an operation to a variable (unary operations)
    public void assign(String a, String unary, String b) {
        Tensor tensorB = variables.get(b);
    if (tensorB == null) {
            operationSuccess = false;
            return;
        }
    if (unary.equals("shape")) {
            int[] dimensions = tensorB.getShape();
            int[] shape = {dimensions.length};
            variables.put(a, new Tensor(shape, dimensions));
        
    } else if (unary.equals("reshape")) {
        // Obtén las dimensiones originales de 'b'
        int[] originalShape = tensorB.getShape();
        
        // Calcula las nuevas dimensiones como la versión invertida de las originales
        int[] newShape = new int[originalShape.length];
        for (int i = 0; i < originalShape.length; i++) {
            newShape[i] = originalShape[originalShape.length - 1 - i];
        }
        // Realiza el reshape y asigna el resultado a 'a'
        Tensor tensorA = tensorB.reshape(newShape);
        variables.put(a, tensorA);
    } else if (unary.equals("shuffle")) {
        //'b' es la variable que se va a barajar
        Tensor tensorA = tensorB.shuffle();
        variables.put(a, tensorA);
    } else {
        operationSuccess = false; // Operación no válida
    }
        
    }

    // Assigns the value of an operation to a variable (unary operations with parameters)
        public void assign(String a, String unary, String b, int[] parameters) {
        Tensor tensorB = variables.get(b);
    
        if (unary.equals("slice")) {
            if (parameters.length % 2 != 0) {
                operationSuccess = false; // El número de parámetros debe ser par (inicio y fin para cada dimensión).
                return;
            }
    
            int numDimensions = parameters.length / 2;
            int[] start = new int[numDimensions];
            int[] end = new int[numDimensions];
    
            for (int i = 0; i < numDimensions; i++) {
                start[i] = parameters[i * 2];
                end[i] = parameters[i * 2 + 1];
            }
    
            // Realiza el corte en tensorB usando los rangos definidos por start y end
            Tensor slicedTensor = tensorB.slice(start, end);
            variables.put(a, slicedTensor);
        } else if (unary.equals("mean")) {
            if (parameters.length != 1) {
                operationSuccess = false; // Parámetros insuficientes para la operación "mean"
                return;
            }
            int axis = parameters[0];
            Tensor meanTensor = tensorB.mean(axis);
            variables.put(a, meanTensor);
        } else if (unary.equals("find")) {
            if (parameters.length != 1) {
                operationSuccess = false; // Parámetros insuficientes para la operación "find"
                return;
            }
            int value = parameters[0];
            int foundIndex = tensorB.find(value);
            // Crea un tensor con el índice encontrado y asigna el resultado a 'a'
            int[] shape = { 1 };
            int[] values = { foundIndex };
            Tensor indexTensor = new Tensor(shape, values);
            variables.put(a, indexTensor);
        } else {
            operationSuccess = false; // Operación no válida
        }
    }


    // Assigns the value of a simple binary operation to a variable (one to one)
    public void assign(String a, String b, String sBinary, String c) {
        
    }

    // Returns the string representation of a tensor
    public String toString(String variable) {
        Tensor tensor = variables.get(variable);
        return (tensor != null) ? tensor.toString() : null;
    }

    // If the last operation was successful
    public boolean ok() {
        return operationSuccess;
    }

    // Calculate the size of a tensor based on its shape
    private int calculateSize(int[] shape) {
        int size = 1;
        for (int dim : shape) {
            size *= dim;
        }
        return size;
    }
    
    public Tensor getValue(String name) {
        return variables.get(name);
    }
}


    


