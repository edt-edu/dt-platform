package ma2fenix;

import arcbasis._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import jsweet.lang.Erased;
import vacuumgripperdashboard.*;

import java.util.*;
import java.util.stream.Collectors;

// TODO: Use from jar, once MontiGem 3 module system is implemented
@Erased
public class MA2FenixTransformer {
  protected Stack<String> stack;
  protected List<Type> types;
  protected Map<String, Category> categories;
  protected List<FFunction> functions;
  protected List<Port> ports;
  protected final Data data;
  protected HashMap<String, Channel> channelMap;

  public MA2FenixTransformer(){
    channelMap = new HashMap<>();
    stack = new Stack();
    types = new ArrayList();
    functions = new ArrayList();
    ports = new ArrayList();
    categories = new HashMap<>();
    data = VacuumGripperDashboardManager.dataBuilder().build().get();
  }

  public void buildFunctionStructure() {
    buildTypes();
    buildCategories();
    buildChannels();
    buildPorts();
    buildFunctions();
  }

  public void transform(ASTComponentType node){
    createFunction(node);
    buildFunctionStructure();
  }

  public String getQname(){
    String qname = "";
    for(String name : stack){
      if(!qname.isEmpty()) qname +=".";
      qname += name;
    }
    return qname;
  }
  public FFunction createFunction(ASTComponentType node){
    if(this.getQname().isEmpty()) {
      stack.push(node.getName().toLowerCase());
    }
    FFunctionBuilder function = VacuumGripperDashboardManager.fFunctionBuilder().name(stack.peek());

    for (ASTConnector c : node.getConnectors()){
      mapConnectorToChannel(c);
    }

    if(node.getSymbol().isDecomposed()){
      for(ASTComponentInstance instance : node.getSubComponents()) {
        stack.push(instance.getName());
        function.subFunctionAdd(createFunction(node.getSpannedScope().resolveComponentType(
            instance.getSymbol().getType().getTypeInfo().getName()
        ).get().getAstNode()));
        stack.pop();
      }
    }

    List<ASTArcPort> inports = node.getPorts().stream().filter(x -> x.getSymbol().isIncoming()).collect(Collectors.toList());
    List<ASTArcPort> outports = node.getPorts().stream().filter(x -> x.getSymbol().isOutgoing()).collect(Collectors.toList());

    for (ASTArcPort inport : inports) {
      function.inAdd(createPort(inport));
    }
    for(ASTArcPort outport : outports){
      function.outAdd(createPort(outport));
    }
    functions.add(function);
    if(getQname().isEmpty()){
      stack.pop();
      buildFunctionStructure();
    }
    return function;
  }

  public Category createCategory(ASTArcPort node){
    Category category;
    if(categories.containsKey(node.getSymbol().getTypeInfo().getFullName())){
      category = categories.get(node.getSymbol().getTypeInfo().getFullName());
    }else {
      category = VacuumGripperDashboardManager.categoryBuilder()
          .type(createType(node.getSymbol().getTypeInfo())).kind(data);
      categories.put(node.getSymbol().getTypeInfo().getFullName(), category);
    }
    return category;
  }

  public Type createType(TypeSymbol node){
    Type type = VacuumGripperDashboardManager.typeBuilder().mctype(node.getFullName());
    types.add(type);
    return type;
  }

  public Port createPort(ASTArcPort node){
    PortBuilder port = VacuumGripperDashboardManager.portBuilder().name(node.getName());
    if(channelMap.containsKey(getQname()+"."+node.getName())){
      port.channel(channelMap.get(getQname()+"."+node.getName()));
    }else{
      Log.error("Port " + this.getQname()+"."+port.getName()+ " does not span a channel");
    }
    ports.add(port);
    return port;
  }

  public void mapConnectorToChannel(ASTConnector connector){
    ASTPortAccess src = connector.getSource();
    for (ASTPortAccess tgt : connector.getTargetList()) {
      if (src.isPresentComponent() && tgt.isPresentComponent()) {
        createChannel(src, tgt);
      } else if (!src.isPresentComponent() && tgt.isPresentComponent()) {
        createChannel(tgt, src);
      } else if (src.isPresentComponent() && !tgt.isPresentComponent()) {
        createChannel(src, tgt);
      }
    }
  }
  public void createChannel(ASTPortAccess key, ASTPortAccess value) {
    if (channelMap.containsKey(this.getQname() + "." + value.getQName())) {
      if (channelMap.containsKey(this.getQname() + "." + key.getQName())) {
        Channel del = channelMap.get(this.getQname() + "." + value.getQName());
        for (Map.Entry<String, Channel> entry : channelMap.entrySet()) {
          if (entry.getValue().equals(del)) {
            channelMap.put(entry.getKey(), channelMap.get(this.getQname() + "." + key.getQName()));
          }
        }
      } else {
        channelMap.put(this.getQname() + "." + key.getQName(), channelMap.get(this.getQname() + "." + value.getQName()));
      }
    } else if (channelMap.containsKey(this.getQname() + "." + key.getQName())){
      channelMap.put(this.getQname() + "." + value.getQName(), channelMap.get(this.getQname() + "." + key.getQName()));
    }else{
      Channel c = VacuumGripperDashboardManager.channelBuilder().category(createCategory(key.getPortSymbol().getAstNode()));
      channelMap.put(this.getQname()+"."+key.getQName(), c);
      channelMap.put(this.getQname()+"."+value.getQName(), c);
    }
  }

  public void buildChannels(){
    for(Channel c : new HashSet<>(channelMap.values())){
      c.build();
    }
  }

  public void buildTypes(){
    for(Type t : types){
      t.build();
    }
  }

  public void buildFunctions(){
    for(FFunction f : functions){
      f.build();
    }
  }

  public void buildPorts(){
    for(Port p : ports){
      p.build();
    }
  }

  public void buildCategories(){
    for(Category c : categories.values()){
      c.build();
    }
  }

}
