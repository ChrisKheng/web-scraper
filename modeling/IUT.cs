using System;
using System.Collections.Generic;
using System.Text;
using System.Linq;

//the namespace must be PAT.Lib, the class and method names can be arbitrary
namespace PAT.Lib
{
    /// <summary>
    /// You can use static library in PAT model.
    /// All methods should be declared as public static.
    /// 
    /// The parameters must be of type "int", "bool", "int[]" or user defined data type
    /// The number of parameters can be 0 or many
    /// 
    /// The return type can be void, bool, int, int[] or user defined data type
    /// 
    /// The method name will be used directly in your model.
    /// e.g. call(max, 10, 2), call(dominate, 3, 2), call(amax, [1,3,5]),
    /// 
    /// Note: method names are case sensetive
    /// </summary>
    
    public class IndexURLTreeHelper {
    	private static List<int> tree = new List<int>();
    	
    	public static void resetTree() {
    		tree = new List<int>();
    	}
    	
    	public static int isDuplicate(int url) {
    		return tree.Contains(url) ? 1 : 0;
    	}
    	
    	public static void addToTree(int url) {
    		tree.Add(url);
    	}
    	
    	public static int checkForDuplicates() {
    		var anyDuplicate = tree.GroupBy(x => x).Any(g => g.Count() > 1);
    		return anyDuplicate ? 1 : 0;
    		
    	}
    }

}
